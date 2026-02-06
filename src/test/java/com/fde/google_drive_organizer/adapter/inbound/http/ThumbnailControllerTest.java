package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.usecase.GetThumbnailUC;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ThumbnailControllerTest {

    private final GetThumbnailUC getThumbnailUC = mock(GetThumbnailUC.class);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ThumbnailController(getThumbnailUC))
            .build();

    @Test
    @WithMockUser
    void shouldReturnThumbnailWhenFoundAndAuthenticated() throws Exception {
        String fileId = "test-file-id";
        byte[] thumbnailData = new byte[]{1, 2, 3, 4, 5};
        when(getThumbnailUC.execute(fileId)).thenReturn(thumbnailData);

        mockMvc.perform(get("/api/thumbnails/{fileId}", fileId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().exists("Cache-Control"))
                .andExpect(content().bytes(thumbnailData));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenThumbnailNotFoundAndAuthenticated() throws Exception {
        String fileId = "non-existent-file-id";
        when(getThumbnailUC.execute(fileId)).thenReturn(null);

        mockMvc.perform(get("/api/thumbnails/{fileId}", fileId))
                .andExpect(status().isNotFound());
    }

    // Note: This test cannot verify authentication in standalone setup
    // Authentication is tested in integration tests with full Spring Security context
}
