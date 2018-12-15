package vision.tensor;

import org.junit.Test;

import java.io.IOException;

public class TestTextDetector {

    @Test
    public void test() throws IOException {

        TextDetector td = new TextDetector();
        td.detect("/image/cow-and-bird.jpg");
    }
}
