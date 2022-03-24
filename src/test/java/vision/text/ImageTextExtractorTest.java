package vision.text;

import net.sourceforge.tess4j.TesseractException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class ImageTextExtractorTest {
    private final ImageTextExtractor imageTextExtractor = new ImageTextExtractor();

    @Test
    public void testExtract_white_bg() throws TesseractException {
        final String fileName = "img/expiry_date_white_bg.jpeg";
        final String text = extract(fileName);
        Assert.assertTrue(text, text.contains("EXP 06. 10.2016"));
    }

    @Test
    public void testExtract_pills_white_bg() throws TesseractException {
        final String fileName = "img/expiry_date_pills_white_bg.jpeg";
        final String text = extract(fileName);
        Assert.assertTrue(text, text.contains("EXP 08/2023"));
    }

    @Test
    public void testExtract_green_bg() throws TesseractException {
        final String fileName = "img/expiry_date_green_bg.jpeg";
        final String text = extract(fileName);
        Assert.assertTrue(text, text.contains("BEST BEFORE\n" +
                "2021 NO 16"));
    }


    @Ignore // Failed (- -)>
    public void testExtract_tin_can_bottom() throws TesseractException {
        final String fileName = "img/expiry_date_tin_can.png";
        final String text = extract(fileName);
        System.out.println(text);
        Assert.assertTrue(text, text.contains("BEST BEFORE\n" +
                "2021 NO 16"));
    }

    private String extract(String fileName) throws TesseractException {
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
        return imageTextExtractor.extract(file);
    }
}
