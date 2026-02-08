package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.usecase.ExtractDocumentContentUC;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArchiveControllerTest {

    private final ExtractDocumentContentUC extractDocumentContentUC = mock(ExtractDocumentContentUC.class);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ArchiveController(extractDocumentContentUC))
            .build();

    @Test
    @WithMockUser
    void shouldReturnOkWhenArchiveSucceeds() throws Exception {
        String fileId = "test-file-id";
        DocumentContent documentContent = new DocumentContent(fileId, "Sample document text content");
        when(extractDocumentContentUC.execute(fileId)).thenReturn(documentContent);

        mockMvc.perform(get("/api/files/{fileId}/archive", fileId))
                .andExpect(status().isOk());

        verify(extractDocumentContentUC).execute(fileId);
    }
}
