import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;


import java.util.HashMap;



public class CombineBolt extends BaseRichBolt {
    private OutputCollector collector;
    private Map<Integer, Mat> blurredFrames = new HashMap<>();
    private Map<Integer, Mat> sharpenedFrames = new HashMap<>();
    private int frameIndex = 0;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {

        // Define the text file path
        String filePath = "Files/Combined_frames_analysis.txt";
        // Create a File object
        File file = new File(filePath);
        // Check if the file already exists
        if (file.exists()) {
            // Delete the existing file
            file.delete();
        }
        // Initialization method called once before the bolt processes tuples
        this.collector = collector;

        // Define the folder path for saving grayscale and resized frames
        String folderPath = "Files/Combined_frames";

        // Create a Path object
        Path path = Paths.get(folderPath);

        // Check if the folder already exists
        if (Files.exists(path)) {
            // Delete the existing folder and its contents
            try {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Create the folder to store processed frames
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private double totalBrightness = 0;
    private int totalFrames = 0;

    @Override
    public void execute(Tuple tuple) {
        try {
            // Extract information from the incoming tuple
            String tag = tuple.getStringByField("tag");
            int index = tuple.getIntegerByField("index");
            Mat frame = (Mat) tuple.getValueByField("frame");

            // Organize frames based on their processing tag (GaussianBlur or Sharpening)
            if (tag.equals("GaussianBlur")) {
                blurredFrames.put(index, frame);
            } else if (tag.equals("Sharpening")) {
                sharpenedFrames.put(index, frame);
            }

            // If frames for the current index are available for both processing types, combine them
            if (blurredFrames.containsKey(frameIndex) && sharpenedFrames.containsKey(frameIndex)) {
                Mat frame1 = blurredFrames.get(frameIndex);
                Mat frame2 = sharpenedFrames.get(frameIndex);

                // Combine the frames using weighted addition
                Mat combinedFrame = new Mat();
                Core.addWeighted(frame1, 0.5, frame2, 0.5, 0, combinedFrame);

                // Save the combined frame as an image
                Imgcodecs.imwrite("Files/Combined_frames/frame_" + frameIndex + ".jpg", combinedFrame);

                // Calculate the average brightness of the frame
                Scalar avgBrightness = Core.mean(combinedFrame);
                totalBrightness += avgBrightness.val[0];
                totalFrames++;
                frameIndex++;

                // Write the average brightness to the text file
                // Create PrintWriter for the text file to store frame analysis data
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new FileWriter("Files/Combined_frames_analysis.txt",true));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                writer.println("Frame " + frameIndex + ": " + avgBrightness.val[0]);
                writer.close();

                // Remove the frames from the maps
                blurredFrames.remove(frameIndex);
                sharpenedFrames.remove(frameIndex);


                // Emit the combined frame to the next bolt in the topology
                collector.emit(new Values(combinedFrame.clone()));
                System.out.println("CombineBolt processed frame " + frameIndex);
            }
        } catch (Exception e) {
            // Handle errors
            System.err.println("Error in CombineBolt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // This method is called when the topology is shutting down
        try {
            // Calculate and write the overall average brightness to the text file
            double averageBrightness = totalBrightness / totalFrames;
            PrintWriter writer = new PrintWriter(new FileWriter("Files/Combined_frames_analysis.txt", true));
            writer.println("\n" + "----------------------------------------------" + "\n\n" + "Total frames: "
                    + totalFrames + "\n\n" + "----------------------------------------------" + "\n");
            writer.println("Average brightness: " + averageBrightness + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output fields for the next bolt
        declarer.declare(new Fields("combined-frame"));
    }
}

