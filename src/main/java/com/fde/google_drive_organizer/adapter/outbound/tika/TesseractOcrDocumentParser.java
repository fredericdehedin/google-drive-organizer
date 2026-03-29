package com.fde.google_drive_organizer.adapter.outbound.tika;

import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
@EnableConfigurationProperties(OcrConfig.class)
public class TesseractOcrDocumentParser implements DocumentParser {

    private static final Logger log = LoggerFactory.getLogger(TesseractOcrDocumentParser.class);

    private final OcrConfig ocrConfig;

    public TesseractOcrDocumentParser(OcrConfig ocrConfig) {
        this.ocrConfig = ocrConfig;
    }

    @Override
    public String parseToText(InputStream inputStream) throws IOException {
        byte[] pdfBytes = inputStream.readAllBytes();

        if (pdfBytes.length == 0) {
            return "";
        }

        try (RandomAccessReadBuffer pdfBuffer = new RandomAccessReadBuffer(pdfBytes);
             PDDocument document = Loader.loadPDF(pdfBuffer)) {
            if (document.getNumberOfPages() == 0) {
                log.warn("No pages found");
                return "";
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setLanguage(ocrConfig.language());
            tesseract.setDatapath(ocrConfig.tessdataPath());

            PDFRenderer renderer = new PDFRenderer(document);
            StringBuilder ocrText = new StringBuilder();

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
                String pageText = tesseract.doOCR(image);

                if (pageText != null && !pageText.isBlank()) {
                    if (!ocrText.isEmpty()) {
                        ocrText.append(System.lineSeparator());
                    }
                    ocrText.append(pageText.trim());
                }
            }

            return ocrText.toString();
        } catch (TesseractException e) {
            throw new DocumentContentExtractionException("OCR extraction failed", e);
        }
    }
}
