import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Map;

class GaussianBlurBolt extends BaseRichBolt {

    private OutputCollector collector;
    private int frameIndex = 0;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        // Initialization method called once before the bolt processes tuples
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            // Processing logic for Gaussian blur
            Mat frame = (Mat) tuple.getValueByField("frame");
            Mat blurredFrame = new Mat();
            Imgproc.GaussianBlur(frame, blurredFrame, new Size(45, 45), 0);

            // Emit the processed frame with additional information
            collector.emit(new Values("GaussianBlur", frameIndex, blurredFrame));
            frameIndex++;
        } catch (Exception e) {
            // Handle errors
            System.err.println("Error in GaussianBlurBolt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output fields as "tag," "index," and "frame"
        declarer.declare(new Fields("tag", "index", "frame"));
    }
}




