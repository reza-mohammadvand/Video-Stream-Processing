import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class OutputBolt extends BaseRichBolt {

    private VideoWriter writer;
    private OutputCollector collector;
    private Queue<Mat> frameQueue = new LinkedList<>();

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        // Initialization method called once before the bolt processes tuples
        this.collector = collector;

        // Initialize VideoWriter to write frames to a new video file
        this.writer = new VideoWriter("Files/output_video.mp4", VideoWriter.fourcc('M', 'P', '4', '2'), 30, new Size(640, 360));

        // Check if the VideoWriter is successfully opened
        if (!writer.isOpened()) {
            System.err.println("Error: VideoWriter could not be opened.");
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            // Process each incoming tuple (frame)
            Mat frame = (Mat) tuple.getValue(0);

            // Check if the frame is empty
            if (frame.empty()) {
                System.err.println("Error: Frame could not be read.");
                return;
            }

            // Write the frame to the new video file
            writer.write(frame);
        } catch (Exception e) {
            // Handle errors
            System.err.println("Error in OutputBolt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // Cleanup method called when the bolt is being shut down
        // Release resources, in this case, close the VideoWriter
        writer.release();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        // Declare the output fields if any
    }
}

