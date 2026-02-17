package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OcrConfigDefaultTest.TestConfig.class)
class OcrConfigDefaultTest {

    @EnableConfigurationProperties(OcrConfig.class)
    static class TestConfig {}

    @Autowired
    private OcrConfig config;

    @Test
    void shouldUseDefaultLanguageWhenNotConfigured() {
        assertThat(config.language()).isEqualTo("deu+eng");
    }
}
