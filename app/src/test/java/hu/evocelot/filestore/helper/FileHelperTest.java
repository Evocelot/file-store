package hu.evocelot.filestore.helper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.properties.FileStoreProperties;

class FileHelperTest {

    @Mock
    private FileStoreProperties fileStoreProperties;

    @InjectMocks
    private FileHelper fileHelper;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(fileStoreProperties.getBufferSize()).thenReturn(1024);
        when(fileStoreProperties.getStorePath()).thenReturn(System.getProperty("java.io.tmpdir"));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("storeFile should save file and return MD5 hash")
    void testStoreFile_success() throws Exception {
        Path tempFile = Files.createTempFile("test", ".txt");
        tempFile.toFile().deleteOnExit();

        byte[] content = "Hello World!".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);

        String hash = fileHelper.storeFile(tempFile.toString(), inputStream);

        assertNotNull(hash);
        assertTrue(hash.matches("[A-F0-9]+"));
        assertTrue(tempFile.toFile().exists());
    }

    @Test
    @DisplayName("storeFile should throw BaseException for invalid input")
    void testStoreFile_invalidInput() {
        BaseException ex = assertThrows(BaseException.class, () -> fileHelper.storeFile("", null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(ExceptionType.INVALID_INPUT, ex.getExceptionType());
    }

    @Test
    @DisplayName("getFileHash should return correct hash of file")
    void testGetFileHash_success() throws Exception {
        Path tempFile = Files.createTempFile("testhash", ".txt");
        Files.write(tempFile, "Test Data".getBytes());
        tempFile.toFile().deleteOnExit();

        String hash = fileHelper.getFileHash(tempFile.toString());

        assertNotNull(hash);
        assertTrue(hash.matches("[A-F0-9]+"));
    }

    @Test
    @DisplayName("getFileHash should throw BaseException on IOException")
    void testGetFileHash_ioException() {
        BaseException ex = assertThrows(BaseException.class, () -> fileHelper.getFileHash("non-existing-file.txt"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(ExceptionType.CANNOT_CALCULATE_MD5, ex.getExceptionType());
    }

    @Test
    @DisplayName("getFile should write file contents to outputStream")
    void testGetFile_success() throws Exception {
        Path tempFile = Files.createTempFile("testread", ".txt");
        Files.write(tempFile, "Stream Content".getBytes());
        tempFile.toFile().deleteOnExit();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        fileHelper.getFile(tempFile.toString(), outputStream);

        assertEquals("Stream Content", outputStream.toString());
    }

    @Test
    @DisplayName("getFile should throw BaseException on IOException")
    void testGetFile_ioException() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BaseException ex = assertThrows(BaseException.class,
                () -> fileHelper.getFile("non-existing-file.txt", outputStream));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(ExceptionType.CANNOT_READ_FILE, ex.getExceptionType());
    }

    @Test
    @DisplayName("deleteFile should delete existing file")
    void testDeleteFile_success() throws IOException {
        Path tempFile = Files.createTempFile("testdelete", ".txt");
        tempFile.toFile().deleteOnExit();

        fileHelper.deleteFile("", tempFile.getFileName().toString().replace(".txt", ""), "txt");

        assertFalse(Files.exists(tempFile));
    }

    @Test
    @DisplayName("deleteFile should log error if file deletion fails")
    void testDeleteFile_failGracefully() {
        // Just call deleteFile with a non-existing file - no exception should be thrown
        assertDoesNotThrow(() -> fileHelper.deleteFile("nonexistent", "file", "txt"));
    }

    @Test
    @DisplayName("getDirectoryPath should return base path if systemId is blank")
    void testGetDirectoryPath_blankSystemId() {
        String path = fileHelper.getDirectoryPath("");
        assertTrue(path.endsWith(File.separator));
    }

    @Test
    @DisplayName("getDirectoryPath should include systemId if provided")
    void testGetDirectoryPath_withSystemId() {
        String path = fileHelper.getDirectoryPath("system");
        assertTrue(path.contains("system"));
    }

    @Test
    @DisplayName("getFullPath should construct path correctly")
    void testGetFullPath() {
        String fullPath = fileHelper.getFullPath("/tmp/", "filename", "txt");
        assertEquals("/tmp/filename.txt", fullPath);
    }

    @Test
    @DisplayName("createDirectoryIfNotExists should create non-existing directory")
    void testCreateDirectoryIfNotExists_success() throws IOException {
        Path tempDir = Files.createTempDirectory("newdir").resolve("subdir");
        tempDir.toFile().deleteOnExit();

        fileHelper.createDirectoryIfNotExists(tempDir.toString());

        assertTrue(tempDir.toFile().exists());
    }

    @Test
    @DisplayName("createDirectoryIfNotExists should not fail if directory already exists")
    void testCreateDirectoryIfNotExists_alreadyExists() throws IOException {
        Path tempDir = Files.createTempDirectory("existingdir");
        tempDir.toFile().deleteOnExit();

        assertDoesNotThrow(() -> fileHelper.createDirectoryIfNotExists(tempDir.toString()));
    }

    @Test
    @DisplayName("createDirectoryIfNotExists should throw if directory cannot be created")
    void testCreateDirectoryIfNotExists_fail() {
        // Use an invalid path (e.g., empty string)
        assertThrows(RuntimeException.class, () -> fileHelper.createDirectoryIfNotExists(""));
    }
}