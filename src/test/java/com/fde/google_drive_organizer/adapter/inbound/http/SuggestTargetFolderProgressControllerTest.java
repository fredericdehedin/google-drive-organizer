package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SuggestTargetFolderProgressControllerTest {

    // Completes the emitter immediately so async dispatch can finish in the test.
    private final SuggestTargetFolderProgressPublisher publisher = new SuggestTargetFolderProgressPublisher() {
        @Override
        public void subscribe(DriveFileId driveFileId, SseEmitter emitter) {
            emitter.complete();
        }
    };

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new SuggestTargetFolderProgressController(publisher))
            .build();

    @Test
    void shouldReturnTextEventStreamForProgressEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/files/{fileId}/suggest-target-folder/progress", "test-file-id"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
    }
}
