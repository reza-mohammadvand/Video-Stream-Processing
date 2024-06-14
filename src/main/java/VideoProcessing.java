import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

class VideoProcessing {

    // Method to process the input video
    public void processVideo() throws IOException {
        // Open the video file for processing
        VideoCapture cap = new VideoCapture("Files/input_video.mp4");

        // Mat object to store video frames
        Mat frame = new Mat();

        // Initialize frame count and total brightness
        int frameCount = 0;
        double totalBrightness = 0;

        // Define the folder path for saving grayscale and resized frames
        String folderPath = "Files/Original_frames";

        // Create a Path object
        Path path = Paths.get(folderPath);

        // Check if the folder already exists
        if (Files.exists(path)) {
            // Delete the existing folder and its contents
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        // Create the folder to store processed frames
        Files.createDirectories(path);

        // Create PrintWriter for the text file to store frame analysis data
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("Files/Original_frames_analysis.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through each frame in the video
        while (cap.read(frame)) {

            // Save Frames
            Imgcodecs.imwrite("Files/Original_frames/frame_" + frameCount + ".jpg", frame);
            frameCount++;

            // Calculate the average brightness of the frame
            Scalar avgBrightness = Core.mean(frame);
            totalBrightness += avgBrightness.val[0];

            // Write the average brightness to the text file
            writer.println("Frame " + frameCount + ": " + avgBrightness.val[0]);
        }

        // Calculate and write the overall average brightness to the text file
        double averageBrightness = totalBrightness / frameCount;
        writer.println("\n" + "----------------------------------------------" + "\n\n" + "Total frames: "
                + frameCount + "\n\n" + "----------------------------------------------" + "\n");
        writer.println("Average brightness: " + averageBrightness + "\n");
        writer.close();

        // Display total frames and average brightness to the console
        System.out.println("Total frames: " + frameCount);
        System.out.println("Average brightness: " + averageBrightness);
    }
}

