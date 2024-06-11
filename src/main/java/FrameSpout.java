import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

class FrameSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private File[] frameFiles;
    private int currentFrameIndex;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        // Initialize spout with output collector and frame files
        this.collector = collector;
        File dir = new File("Files/Original_frames");
        this.frameFiles = dir.listFiles();

        // Sort frame files based on their numerical order in the filename
        Arrays.sort(this.frameFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                int n1 = extractNumber(f1.getName());
                int n2 = extractNumber(f2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('_') + 1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch (Exception e) {
                    i = 0; // if filename does not match the format, default to 0
                }
                return i;
            }
        });

        // Initialize the frame index
        this.currentFrameIndex = 0;
    }

    @Override
    public void nextTuple() {
        try {
            // Emit the next frame if available
            if (currentFrameIndex < frameFiles.length) {
                Mat frame = Imgcodecs.imread(frameFiles[currentFrameIndex].getPath());
                if (!frame.empty()) {
                    // Convert the frame to grayscale
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    // Resize the grayscale frame
                    Mat resizedFrame = new Mat();
                    Imgproc.resize(grayFrame, resizedFrame, new Size(640, 360));  // Change the size as needed

                    collector.emit(new Values(resizedFrame.clone()));
                    System.out.println("FrameSpout emitted frame " + currentFrameIndex);
                    currentFrameIndex++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in FrameSpout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output field as "frame"
        declarer.declare(new Fields("frame"));
    }
}
