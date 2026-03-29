package com.fde.google_drive_organizer.adapter.outbound.tika;

import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.ZeroByteFileException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaPdfTextDocumentParser implements DocumentParser {

    private final Tika tika = new Tika();

    @Override
    public String parseToText(InputStream inputStream) throws IOException {
        try {
            return tika.parseToString(inputStream);
        } catch (ZeroByteFileException e) {
            return "";
        } catch (TikaException e) {
            throw new DocumentContentExtractionException("Text extraction failed", e);
        }
    }
}
