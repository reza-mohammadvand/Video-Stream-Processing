import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;


class StormTopology {

    // Method to run the Apache Storm topology for distributed image processing
    public void runStormTopology() throws Exception {
        // Build Topology
        TopologyBuilder builder = new TopologyBuilder();

        // Define the spout to read frames from the video
        builder.setSpout("frame-spout", new FrameSpout(), 1);

        // Define bolts for Gaussian blur and sharpening operations
        builder.setBolt("gaussian-blur-bolt", new GaussianBlurBolt(), 1).shuffleGrouping("frame-spout");
        builder.setBolt("sharpening-bolt", new SharpeningBolt(), 1).shuffleGrouping("frame-spout");

        // Define a bolt to combine the output of Gaussian blur and sharpening bolts
        builder.setBolt("combine-bolt", new CombineBolt(), 1).shuffleGrouping("gaussian-blur-bolt").shuffleGrouping("sharpening-bolt");

        // Define a bolt to handle the final output
        builder.setBolt("output-bolt", new OutputBolt(), 1).shuffleGrouping("combine-bolt");

        // Configuration
        Config conf = new Config();
        conf.setDebug(true);

        // Run Topology Locally using a LocalCluster
        LocalCluster cluster = new LocalCluster();
        try {
            // Submit the topology and let it run for 10 seconds (adjust as needed)
            cluster.submitTopology("video-processing-topology", conf, builder.createTopology());
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Shutdown the cluster after processing
            cluster.shutdown();
        }
    }
}



















