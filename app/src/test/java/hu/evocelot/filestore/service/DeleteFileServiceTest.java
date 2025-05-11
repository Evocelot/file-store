package hu.evocelot.filestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.model.FileEntity;

class DeleteFileServiceTest {

    @Mock
    private FileEntityAccessor fileEntityAccessor;

    @Mock
    private FileHelper fileHelper;

    @InjectMocks
    private DeleteFileService deleteFileService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("deleteFile should delete file and metadata successfully")
    void testDeleteFile_success() throws BaseException {
        // Arrange
        String fileId = "test-file-id";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setSystemId("system1");
        fileEntity.setExtension("txt");

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));

        // Act
        ResponseEntity<Void> response = deleteFileService.deleteFile(fileId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fileHelper).deleteFile(fileEntity.getSystemId(), fileEntity.getId(), fileEntity.getExtension());
        verify(fileEntityAccessor).delete(fileEntity);
    }

    @Test
    @DisplayName("deleteFile should throw BaseException if file not found")
    void testDeleteFile_fileNotFound() {
        // Arrange
        String fileId = "nonexistent-file-id";
        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.empty());

        // Act + Assert
        BaseException ex = assertThrows(BaseException.class, () -> deleteFileService.deleteFile(fileId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(ExceptionType.FILE_ENTITY_NOT_FOUND, ex.getExceptionType());
        assertTrue(ex.getMessage().contains(fileId));

        verify(fileEntityAccessor).findById(fileId);
        verifyNoMoreInteractions(fileEntityAccessor);
        verifyNoInteractions(fileHelper);
    }
}