package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

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
    public String parseToText(InputStream inputStream) throws IOException, TikaException {
        try {
            TesseractOCRConfig tesseractConfig = new TesseractOCRConfig();
            tesseractConfig.setLanguage(ocrConfig.language());

            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setExtractInlineImages(true);
            pdfConfig.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.OCR_ONLY);

            ParseContext parseContext = new ParseContext();
            parseContext.set(TesseractOCRConfig.class, tesseractConfig);
            parseContext.set(PDFParserConfig.class, pdfConfig);

            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();

            parser.parse(inputStream, handler, metadata, parseContext);

            String ocrText = handler.toString();
            log.debug("OCR extraction completed, extracted {} characters", ocrText.length());
            return ocrText;

        } catch (ZeroByteFileException e) {
            return "";
        } catch (SAXException e) {
            throw new TikaException("SAX parsing error during OCR extraction", e);
        }
    }
}
