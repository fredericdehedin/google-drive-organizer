package com.fde.google_drive_organizer.adapter.inbound.http;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MoveFileControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new MoveFileController())
            .build();

    @Test
    void shouldReturnOkWhenMovingFile() throws Exception {
        mockMvc.perform(get("/api/files/{fileId}/move", "test-file-id")
                        .param("folderName", "Invoices 2024"))
                .andExpect(status().isOk());
    }
}
