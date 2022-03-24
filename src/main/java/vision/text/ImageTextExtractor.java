package vision.text;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class ImageTextExtractor {

    static {
        System.setProperty("jna.library.path", "/usr/local/lib");
    }

    private final ITesseract instance = new Tesseract();

    public String extract(File imageFile) throws TesseractException {
        return instance.doOCR(imageFile);
    }
}
