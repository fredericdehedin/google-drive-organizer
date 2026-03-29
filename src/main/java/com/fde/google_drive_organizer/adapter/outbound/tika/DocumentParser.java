package com.fde.google_drive_organizer.adapter.outbound.tika;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentParser {
    String parseToText(InputStream inputStream) throws IOException;
}
