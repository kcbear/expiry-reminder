package vision.tensor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import vision.tensor.classifier.YOLOClassifier;
import vision.tensor.graph.GraphBuilder;
import vision.tensor.model.Recognition;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class TextDetector {

    private final static Logger LOGGER = LoggerFactory.getLogger(TextDetector.class);
    private byte[] frozenGraph;
    private List<String> LABELS;
    private int SIZE = 416;
    private float MEAN = 255f;


    public TextDetector() {
        try {
            frozenGraph = IOUtils.toByteArray(this.getClass().getResourceAsStream("/tiny-yolo-voc.pb"));
            File file = new File(TextDetector.class.getResource("/yolo-voc-labels.txt").toURI());
            LABELS = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));

        } catch (Exception ex) {
            LOGGER.error("Download one of my graph file to run the program! \n" +
                    "You can find my graphs here: https://drive.google.com/open?id=1GfS1Yle7Xari1tRUEi2EDYedFteAOaoN");
        }
    }


    public void detect(final String imageLocation) throws IOException {
        byte[] image = IOUtils.toByteArray(this.getClass().getResourceAsStream(imageLocation));
        try (Tensor<Float> normalizedImage = normalizeImage(image)) {
            List<Recognition> recognitions = YOLOClassifier.getInstance().classifyImage(executeYOLOGraph(frozenGraph, normalizedImage), LABELS);
            printToConsole(recognitions);
//            ImageUtil.getInstance().labelImage(image, recognitions, IOUtil.getFileName(imageLocation));
        }
    }

    private Tensor<Float> normalizeImage(final byte[] imageBytes) {
        try (Graph graph = new Graph()) {

            GraphBuilder graphBuilder = new GraphBuilder(graph);

            final Output<Float> output =
                    graphBuilder.div( // Divide each pixels with the MEAN
                            graphBuilder.resizeBilinear( // Resize using bilinear interpolation
                                    graphBuilder.expandDims( // Increase the output tensors dimension
                                            graphBuilder.cast( // Cast the output to Float
                                                    graphBuilder.decodeJpeg(
                                                            graphBuilder.constant("input", imageBytes), 3),
                                                    Float.class),
                                            graphBuilder.constant("make_batch", 0)),
                                    graphBuilder.constant("size", new int[]{SIZE, SIZE})),
                            graphBuilder.constant("scale", MEAN));


            try (Session session = new Session(graph)) {
                return session.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
            }
        }
    }

    /**
     * Executes graph on the given preprocessed image
     *
     * @param normalizedImage preprocessed image
     * @return output tensor returned by tensorFlow
     */
    private float[] executeYOLOGraph(byte[] frozenGraph, final Tensor<Float> normalizedImage) {
        try (Graph graph = new Graph()) {
            graph.importGraphDef(frozenGraph);
            try (Session s = new Session(graph);
                 Tensor<Float> result = s.runner().feed("input", normalizedImage).fetch("output").run().get(0).expect(Float.class)) {
                float[] outputTensor = new float[YOLOClassifier.getInstance().getOutputSizeByShape(result)];
                FloatBuffer floatBuffer = FloatBuffer.wrap(outputTensor);
                result.writeTo(floatBuffer);
                return outputTensor;
            }
        }
    }


    /**
     * Prints out the recognize objects and its confidence
     *
     * @param recognitions list of recognitions
     */
    private void printToConsole(final List<Recognition> recognitions) {
        for (Recognition recognition : recognitions) {
            LOGGER.info("Object: {} - confidence: {}", recognition.getTitle(), recognition.getConfidence());
        }
    }


}

