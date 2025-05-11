package hu.evocelot.filestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.model.FileEntity;

class DownloadFileServiceTest {

    @Mock
    private FileEntityAccessor fileEntityAccessor;

    @Mock
    private FileHelper fileHelper;

    @InjectMocks
    private DownloadFileService downloadFileService;

    private AutoCloseable closeable;

    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        // Create a temporary file for testing
        tempFile = File.createTempFile("testfile", ".txt");
        Files.write(tempFile.toPath(), "Test content".getBytes());
        tempFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    @DisplayName("downloadFile should successfully return file without hash checking")
    void testDownloadFile_success_noHashCheck() throws Exception {
        // Arrange
        String fileId = "fileId123";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setSystemId("systemId");
        fileEntity.setExtension("txt");
        fileEntity.setName("testfile");

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileHelper.getDirectoryPath(fileEntity.getSystemId())).thenReturn(tempFile.getParent());
        when(fileHelper.getFullPath(tempFile.getParent(), fileId, fileEntity.getExtension()))
                .thenReturn(tempFile.getAbsolutePath());

        doAnswer(invocation -> {
            String path = invocation.getArgument(0);
            OutputStream out = invocation.getArgument(1);
            out.write(Files.readAllBytes(Path.of(path)));
            return null;
        }).when(fileHelper).getFile(anyString(), any(OutputStream.class));

        // Act
        ResponseEntity<StreamingResponseBody> response = downloadFileService.downloadFile(fileId, false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("attachment; filename=\"testfile.txt\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(tempFile.length(), response.getHeaders().getContentLength());

        // Test StreamingResponseBody
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getBody().writeTo(outputStream);
        assertEquals("Test content", outputStream.toString());
    }

    @Test
    @DisplayName("downloadFile should successfully return file with hash checking")
    void testDownloadFile_success_withHashCheck() throws Exception {
        // Arrange
        String fileId = "fileId123";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setSystemId("systemId");
        fileEntity.setExtension("txt");
        fileEntity.setName("testfile");
        fileEntity.setHash("D41D8CD98F00B204E9800998ECF8427E"); // fake hash for test

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileHelper.getDirectoryPath(fileEntity.getSystemId())).thenReturn(tempFile.getParent());
        when(fileHelper.getFullPath(tempFile.getParent(), fileId, fileEntity.getExtension()))
                .thenReturn(tempFile.getAbsolutePath());
        when(fileHelper.getFileHash(tempFile.getAbsolutePath())).thenReturn(fileEntity.getHash());

        // Act
        ResponseEntity<StreamingResponseBody> response = downloadFileService.downloadFile(fileId, true);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Streaming check
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getBody().writeTo(outputStream);
    }

    @Test
    @DisplayName("downloadFile should throw BaseException if file not found in database")
    void testDownloadFile_fileEntityNotFound() {
        // Arrange
        String fileId = "missingId";
        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.empty());

        // Act + Assert
        BaseException ex = assertThrows(BaseException.class, () -> downloadFileService.downloadFile(fileId, false));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(ExceptionType.FILE_ENTITY_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @DisplayName("downloadFile should throw BaseException if file does not exist or is unreadable")
    void testDownloadFile_fileNotExistOrUnreadable() {
        // Arrange
        String fileId = "fileId123";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setSystemId("systemId");
        fileEntity.setExtension("txt");
        fileEntity.setName("testfile");

        File nonExistentFile = new File("nonexistentfile.txt");

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileHelper.getDirectoryPath(fileEntity.getSystemId())).thenReturn(nonExistentFile.getParent());
        when(fileHelper.getFullPath(nonExistentFile.getParent(), fileId, fileEntity.getExtension()))
                .thenReturn(nonExistentFile.getAbsolutePath());

        // Act + Assert
        BaseException ex = assertThrows(BaseException.class, () -> downloadFileService.downloadFile(fileId, false));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(ExceptionType.FILE_ENTITY_NOT_FOUND, ex.getExceptionType());
    }

    @Test
    @DisplayName("downloadFile should throw BaseException if hash mismatch detected")
    void testDownloadFile_hashMismatch() throws Exception {
        // Arrange
        String fileId = "fileId123";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setSystemId("systemId");
        fileEntity.setExtension("txt");
        fileEntity.setName("testfile");
        fileEntity.setHash("EXPECTED_HASH");

        when(fileEntityAccessor.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileHelper.getDirectoryPath(fileEntity.getSystemId())).thenReturn(tempFile.getParent());
        when(fileHelper.getFullPath(tempFile.getParent(), fileId, fileEntity.getExtension()))
                .thenReturn(tempFile.getAbsolutePath());
        when(fileHelper.getFileHash(tempFile.getAbsolutePath())).thenReturn("ACTUAL_WRONG_HASH");

        // Act + Assert
        BaseException ex = assertThrows(BaseException.class, () -> downloadFileService.downloadFile(fileId, true));
        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(ExceptionType.CORRUPTED_FILE, ex.getExceptionType());
    }
}