package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.MoveDocumentToFolder;
import com.fde.google_drive_organizer.progress.FileId;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArchiveProgressControllerTest {

    // Completes the emitter immediately so async dispatch can finish in the test.
    private final ProgressEventPublisher publisher = new ProgressEventPublisher() {
        @Override
        public void subscribe(FileId fileId, SseEmitter emitter) {
            emitter.complete();
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ArchiveController(mock(MoveDocumentToFolder.class), publisher))
            .build();

    @Test
    void shouldReturnTextEventStreamForProgressEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/files/{fileId}/archive/progress", "test-file-id"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
    }
}
