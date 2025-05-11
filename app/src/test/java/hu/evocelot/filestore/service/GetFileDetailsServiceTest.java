package hu.evocelot.filestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import hu.evocelot.filestore.converter.FileEntityWithIdConverter;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.model.FileEntity;

class GetFileDetailsServiceTest {

    @Mock
    private FileEntityAccessor fileEntityAccessor;

    @Mock
    private FileEntityWithIdConverter fileEntityWithIdConverter;

    @InjectMocks
    private GetFileDetailsService getFileDetailsService;

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
    @DisplayName("getFileDetails should return file details successfully")
    void testGetFileDetails_success() throws BaseException {
        // Arrange
        String fileId = "test-file-id";
        FileEntity fileEntity = new FileEntity();
        FileEntityWithIdDto dto = new FileEntityWithIdDto();

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileEntityWithIdConverter.convert(fileEntity)).thenReturn(dto);

        // Act
        ResponseEntity<FileEntityWithIdDto> response = getFileDetailsService.getFileDetails(fileId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());

        verify(fileEntityAccessor).findById(fileId);
        verify(fileEntityWithIdConverter).convert(fileEntity);
    }

    @Test
    @DisplayName("getFileDetails should throw BaseException if file not found")
    void testGetFileDetails_fileNotFound() {
        // Arrange
        String fileId = "nonexistent-file-id";
        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.empty());

        // Act + Assert
        BaseException ex = assertThrows(BaseException.class, () -> getFileDetailsService.getFileDetails(fileId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(ExceptionType.FILE_ENTITY_NOT_FOUND, ex.getExceptionType());
        assertTrue(ex.getMessage().contains(fileId));

        verify(fileEntityAccessor).findById(fileId);
        verifyNoInteractions(fileEntityWithIdConverter);
    }
}