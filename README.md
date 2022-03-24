# A Java application that performs OCR text extraction and create a journal item with the expiry date

### Development in MacOS prerequisite:
brew install tesseract 

brew install tesseract-lang

### Notes
1. Surprisingly /usr/local/lib doesn't seem to be checked by JNI on OSX at all and fix it in static stating: 
   1. System.setProperty("jna.library.path", "/usr/local/lib");
   
2. tesseract-lang does not contain the default eng package and thus manually copy it to the TESSDATA_PREFIX folder:
   1. cp -rp /usr/local/Cellar/tesseract/5.1.0/share/tessdata/eng.traineddata /usr/local/Cellar/tesseract-lang/4.1.0/share/tessdata/

### To Run:
1. set env variable:
   1. TESSDATA_PREFIX=/usr/local/Cellar/tesseract-lang/4.1.0/share/tessdata


### Reference:
https://www.baeldung.com/java-ocr-tesseract

http://tess4j.sourceforge.net/
