package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.outbound.FileRepository;
import com.fde.google_drive_organizer.domain.drive_file.file.DriveFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fde.google_drive_organizer.domain.drive_file.DriveFileTestFixture.aDriveFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRootFolderDriveFilesUCTest {

    @Mock
    private FileRepository fileRepository;

    private ListRootFolderDriveFilesUC useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListRootFolderDriveFilesUC(fileRepository);
    }

    @Test
    void shouldReturnFilesFromRootFolder() {
        DriveFile file1 = aDriveFile().withId("id1").withName("file1.txt").build();
        DriveFile file2 = aDriveFile().withId("id2").withName("file2.txt").build();
        List<DriveFile> expectedFiles = List.of(file1, file2);

        when(fileRepository.getFilesInRootFolder()).thenReturn(expectedFiles);

        List<DriveFile> actualFiles = useCase.list();

        assertThat(actualFiles).isEqualTo(expectedFiles);
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesInRootFolder() {
        when(fileRepository.getFilesInRootFolder()).thenReturn(List.of());

        List<DriveFile> actualFiles = useCase.list();

        assertThat(actualFiles).isEmpty();
    }
}
