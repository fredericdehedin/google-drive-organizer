package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.domain.port.outbound.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fde.google_drive_organizer.domain.model.DriveFileTestFixture.aDriveFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDriveFilesUCTest {

    @Mock
    private FileRepository fileRepository;

    private ListDriveFilesUC useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListDriveFilesUC(fileRepository);
    }

    @Test
    void shouldReturnFilesFromCheckInFolder() {
        DriveFile file1 = aDriveFile().withId("id1").withName("file1.txt").build();
        DriveFile file2 = aDriveFile().withId("id2").withName("file2.txt").build();
        List<DriveFile> expectedFiles = List.of(file1, file2);

        when(fileRepository.getFilesInCheckInFolder()).thenReturn(expectedFiles);

        List<DriveFile> actualFiles = useCase.execute();

        assertThat(actualFiles).isEqualTo(expectedFiles);
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesInCheckInFolder() {
        when(fileRepository.getFilesInCheckInFolder()).thenReturn(List.of());

        List<DriveFile> actualFiles = useCase.execute();

        assertThat(actualFiles).isEmpty();
    }
}
