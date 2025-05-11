package hu.evocelot.filestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.converter.FileEntityWithIdConverter;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.dto.FileUploadRequestDto;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.kafka.KafkaMessageProducer;
import hu.evocelot.filestore.kafka.KafkaTopics;
import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.properties.KafkaProperties;

class UploadFileServiceTest {

    @Mock
    private FileEntityWithIdConverter fileEntityWithIdConverter;

    @Mock
    private FileEntityAccessor fileEntityAccessor;

    @Mock
    private FileHelper fileHelper;

    @Mock
    private KafkaProperties kafkaProperties;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaMessageProducer kafkaMessageProducer;

    @InjectMocks
    private UploadFileService uploadFileService;

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
    @DisplayName("uploadFile should upload file and send kafka message when kafka is enabled")
    void testUploadFile_kafkaEnabled() throws Exception {
        // Arrange
        FileUploadRequestDto fileUploadRequestDto = mock(FileUploadRequestDto.class);
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = new ByteArrayInputStream("Test content".getBytes());

        when(fileUploadRequestDto.getName()).thenReturn("testfile");
        when(fileUploadRequestDto.getExtension()).thenReturn("txt");
        when(fileUploadRequestDto.getObjectId()).thenReturn("object123");
        when(fileUploadRequestDto.getSystemId()).thenReturn("system1");
        when(fileUploadRequestDto.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        FileEntity initialEntity = new FileEntity();
        initialEntity.setId("file-id-123");

        FileEntity savedEntity = new FileEntity();
        savedEntity.setId("file-id-123");
        savedEntity.setHash("ABCD1234");

        when(fileEntityAccessor.save(any(FileEntity.class))).thenReturn(initialEntity).thenReturn(savedEntity);
        when(fileHelper.getDirectoryPath(anyString())).thenReturn("/tmp/system1/");
        when(fileHelper.getFullPath(anyString(), anyString(), anyString())).thenReturn("/tmp/system1/file-id-123.txt");
        when(fileHelper.storeFile(anyString(), any(InputStream.class))).thenReturn("ABCD1234");
        when(kafkaProperties.getEnabled()).thenReturn("true");
        when(objectMapper.writeValueAsString(any(FileEntity.class))).thenReturn("{\"mocked\":\"json\"}");

        FileEntityWithIdDto dto = new FileEntityWithIdDto();
        when(fileEntityWithIdConverter.convert(savedEntity)).thenReturn(dto);

        // Act
        ResponseEntity<FileEntityWithIdDto> response = uploadFileService.uploadFile(fileUploadRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());

        verify(fileEntityAccessor, times(2)).save(any(FileEntity.class));
        verify(fileHelper).createDirectoryIfNotExists("/tmp/system1/");
        verify(fileHelper).storeFile(anyString(), any(InputStream.class));
        verify(kafkaMessageProducer).sendMessage(KafkaTopics.FILE_SAVED, "{\"mocked\":\"json\"}");
    }

    @Test
    @DisplayName("uploadFile should upload file without sending kafka message when kafka is disabled")
    void testUploadFile_kafkaDisabled() throws Exception {
        // Arrange
        FileUploadRequestDto fileUploadRequestDto = mock(FileUploadRequestDto.class);
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = new ByteArrayInputStream("Test content".getBytes());

        when(fileUploadRequestDto.getName()).thenReturn("testfile");
        when(fileUploadRequestDto.getExtension()).thenReturn("txt");
        when(fileUploadRequestDto.getObjectId()).thenReturn("object123");
        when(fileUploadRequestDto.getSystemId()).thenReturn("system1");
        when(fileUploadRequestDto.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        FileEntity initialEntity = new FileEntity();
        initialEntity.setId("file-id-123");

        FileEntity savedEntity = new FileEntity();
        savedEntity.setId("file-id-123");
        savedEntity.setHash("ABCD1234");

        when(fileEntityAccessor.save(any(FileEntity.class))).thenReturn(initialEntity).thenReturn(savedEntity);
        when(fileHelper.getDirectoryPath(anyString())).thenReturn("/tmp/system1/");
        when(fileHelper.getFullPath(anyString(), anyString(), anyString())).thenReturn("/tmp/system1/file-id-123.txt");
        when(fileHelper.storeFile(anyString(), any(InputStream.class))).thenReturn("ABCD1234");
        when(kafkaProperties.getEnabled()).thenReturn("false");

        FileEntityWithIdDto dto = new FileEntityWithIdDto();
        when(fileEntityWithIdConverter.convert(savedEntity)).thenReturn(dto);

        // Act
        ResponseEntity<FileEntityWithIdDto> response = uploadFileService.uploadFile(fileUploadRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());

        verify(fileEntityAccessor, times(2)).save(any(FileEntity.class));
        verify(fileHelper).createDirectoryIfNotExists("/tmp/system1/");
        verify(fileHelper).storeFile(anyString(), any(InputStream.class));
        verify(kafkaMessageProducer, never()).sendMessage(anyString(), anyString());
    }
}