package no.nav.foreldrepenger.selvbetjening.rest.util;

import static com.itextpdf.text.PageSize.A4;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;
import static org.springframework.util.StreamUtils.copyToByteArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class ImageByteArray2PdfConverter {

    private final PDFPageSplitter pdfPageSplitter;
    private final Pdf2ImageConverter pdf2ImageConverter;

    private static final Logger LOG = LoggerFactory.getLogger(ImageByteArray2PdfConverter.class);

    private final List<MediaType> supportedMediaTypes;

    @Inject
    public ImageByteArray2PdfConverter(PDFPageSplitter splitter, Pdf2ImageConverter converter) {
        this(splitter, converter, IMAGE_JPEG, IMAGE_PNG);
    }

    public ImageByteArray2PdfConverter(PDFPageSplitter splitter, Pdf2ImageConverter converter,
            MediaType... mediaTypes) {
        this(splitter, converter, Arrays.asList(mediaTypes));
    }

    public ImageByteArray2PdfConverter(PDFPageSplitter splitter, Pdf2ImageConverter converter,
            List<MediaType> mediaTypes) {
        this.pdfPageSplitter = splitter;
        this.pdf2ImageConverter = converter;
        this.supportedMediaTypes = mediaTypes;
    }

    public byte[] convert(String classPathResource) {
        return convert(classPathResource, false);
    }

    public byte[] convert(String classPathResource, boolean compress) {
        try {
            return convert(new ClassPathResource(classPathResource), compress);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public byte[] convert(Resource resource) throws IOException {
        return convert(resource, false);
    }

    public byte[] convert(Resource resource, boolean compress) throws IOException {
        return convert(copyToByteArray(resource.getInputStream()), compress);
    }

    public byte[] convert(byte[] bytes) {
        return convert(bytes, false);
    }

    public byte[] convert(byte[] bytes, boolean compress) {
        MediaType mediaType = mediaType(bytes);
        if (APPLICATION_PDF.equals(mediaType)) {
            LOG.info("Innhold er allerede PDF, deles opp i bildedeler");
            List<byte[]> pdfPages = pdfPageSplitter.split(bytes);
            LOG.info("PDF ble delt opp i {} deler", pdfPages.size());
            return embedImagesInPdf(compress, pdf2ImageConverter.convertToImages(pdfPages));
        }
        if (shouldConvertImage(mediaType)) {
            return embedImagesInPdf(compress, bytes);
        }
        throw new UnsupportedAttachmentTypeException(mediaType);
    }

    private byte[] embedImagesInPdf(boolean compress, byte[]... images) {
        return embedImagesInPdf(compress, Arrays.asList(images));
    }

    private byte[] embedImagesInPdf(boolean compress, List<byte[]> images) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            if (compress) {
                writer.setFullCompression();
            }
            document.open();
            for (byte[] image : images) {
                addImage(document, image);

            }
            document.close();
            byte[] converted = baos.toByteArray();
            return converted;
        } catch (Exception e) {
            LOG.warn("Konvertering feilet", e);
            throw new IllegalArgumentException(e);
        }
    }

    public List<MediaType> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    private void addImage(Document document, byte[] bytes)
            throws BadElementException, MalformedURLException, IOException, DocumentException {
        Image image = Image.getInstance(bytes);
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleToFit(A4.getWidth(), A4.getHeight());
        document.add(image);
    }

    private boolean shouldConvertImage(MediaType mediaType) {
        boolean shouldConvert = supportedMediaTypes.contains(mediaType);
        LOG.info("{} convert byte stream of type {} to PDF", shouldConvert ? "Will" : "Will not", mediaType);
        return shouldConvert;
    }

    private MediaType mediaType(byte[] bytes) {
        return MediaType.valueOf(new Tika().detect(bytes));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [splitter=" + pdfPageSplitter + ", supportedMediaTypes="
                + supportedMediaTypes
                + "]";
    }

}
