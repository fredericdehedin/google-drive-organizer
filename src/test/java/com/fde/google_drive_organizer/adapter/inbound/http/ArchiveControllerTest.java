package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.MoveDocumentToFolder;
import com.fde.google_drive_organizer.domain.model.DriveFileTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArchiveControllerTest {

    private final MoveDocumentToFolder moveDocumentToFolder = mock(MoveDocumentToFolder.class);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ArchiveController(moveDocumentToFolder))
            .build();

    @Test
    @WithMockUser
    void shouldReturnOkWhenArchiveSucceeds() throws Exception {
        mockMvc.perform(get("/api/files/{fileId}/archive", "test-file-id")
                        .param("fileName", "invoice.pdf"))
                .andExpect(status().isOk());

        verify(moveDocumentToFolder).move(DriveFileTestFixture.aDriveFile()
                .withId("test-file-id")
                .withName("invoice.pdf")
                .withMimeType(null)
                .withIconLink(null)
                .withThumbnailLink(null)
                .build());
    }
}
