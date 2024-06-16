# Video-Stream-Processing
Video Stream Processing :  A Distributed stream processing
This repository implements a real-time video processing application built using Apache Storm. It leverages distributed computing principles to process video frames in parallel, apply image filters, and aggregate the results.

## Project Overview

The Video-Stream-Processing application is designed to perform several image processing operations on video files using Apache Storm. The application reads a video file, processes each frame by applying filters like Gaussian blur and sharpening, and then combines these frames to produce an output video that highlights the effects of these operations.


## Project Goals

* Design a stream processing application with Apache Storm to perform real-time image processing on video files.
* Showcase distributed computing by processing video frames concurrently and applying filters.
* Aggregate results like average brightness and total frame count.

## Technologies Used

* **Primary Framework:** Apache Storm
* **Programming Language:** Java (recommended) or any other compatible language
* **Additional Library:** OpenCV for Java (image processing)

## Functional Requirements

**1. Video Reading and Analysis**

* Read video files using appropriate mechanisms.
* Extract individual frames from the video stream.
* Count the total number of frames.
* Calculate and record the average brightness of each frame.

**2. Image Processing Operations**

* Convert frames to grayscale for efficient processing.
* Resize frames to a predetermined smaller size for faster processing and potential performance gains.

**3. Distributed Image Filtering with Apache Storm**

* Create a Storm topology to manage distributed processing tasks.
* Design PEs (Processing Elements) for efficient data streaming and processing:
    * **PE1 (GaussianBlurBolt):** Applies a Gaussian blur filter for noise reduction.
    * **PE2 (SharpeningBolt):** Applies a sharpening filter for enhanced image details.

**4. Frame Aggregation**

* Develop a PE (CombineBolt) that combines the outputs from the Gaussian blur and sharpening filters.
* Sum the resulting matrices from the applied filters to create the combined effect.

**5. Output Generation**

* Generate a new video file containing the filtered frames.
* Produce a text file containing frame analysis data (average brightness, frame count).

## Development Guidelines

**1. Effective Apache Storm Utilization**

* Design an Apache Storm topology to manage the distributed processing tasks effectively.
* Ensure efficient data streaming and processing among the PEs for optimal performance.

**2. Prioritize Code Efficiency and Scalability**

* Write clean, maintainable, and well-documented code for easier understanding and future modifications.
* Design the application to be scalable for processing videos of varying sizes without performance bottlenecks.

**3. Performance Optimization**

* Optimize the code to minimize processing latency and ensure real-time video processing capabilities.

## Code Structure

The codebase is organized into several well-defined classes with specific functionalities:

* `VideoProcessing`: Reads a video file, processes each frame (grayscale conversion, resizing, brightness calculation), and stores the results.
* `FrameSpout`: Reads frames from a designated directory, sorts them for proper processing order, and sends them to the bolts for filtering.
* `GaussianBlurBolt` (PE1): Applies a Gaussian blur filter to each received frame for noise reduction.
* `SharpeningBolt` (PE2): Applies a sharpening filter to each frame for enhanced image details.
* `CombineBolt`: Combines the outputs from the Gaussian blur and sharpening bolts, effectively merging the filter effects.
* `OutputBolt`: Writes the processed frames to a new video file and creates a text file containing frame analysis data (average brightness, total frame count).

## Getting Started

1. **Clone the Repository:**
   Use `git clone https://github.com/reza-mohammadvand/Video-Stream-Processing` to clone the repository.

2. **Install Dependencies:**
   Ensure you have Apache Storm and OpenCV for Java installed on your development environment.

3. **Set Up Storm Cluster:**
   Refer to the Apache Storm documentation (https://storm.apache.org/documentation/Home.html) for instructions on setting up a Storm cluster.

4. **Configure the Application:**
   Modify the code to specify your desired video file path and the output directory where the processed video and analysis file will be saved.

5. **Build and Run the Application:**
   Use your preferred Java IDE or command line to build and run the application.

