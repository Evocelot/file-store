package hu.evocelot.filestore.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.evocelot.filestore.converter.FileEntityWithIdConverter;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.dto.FileUploadRequestDto;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.kafka.KafkaMessageProducer;
import hu.evocelot.filestore.kafka.KafkaTopics;
import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.properties.KafkaProperties;
import hu.evocelot.filestore.service.FileService;

/**
 * Action class responsible for handling file uploads.
 * <p>
 * This class encapsulates the logic required to process and store files
 * uploaded to the system.
 * </p>
 * 
 * <h3>Responsibilities:</h3>
 * <ul>
 * <li>Generate a unique filename for the uploaded file.</li>
 * <li>Create necessary directories if they do not exist.</li>
 * <li>Store the uploaded file on the file system.</li>
 * <li>Generate a hash of the file's contents for integrity checking.</li>
 * <li>Convert and save file metadata as an entity in the database.</li>
 * <li>Return a response containing the saved file's metadata and ID.</li>
 * </ul>
 * 
 * @author mark.danisovszky
 */
@Component
public class UploadFileAction {

    @Autowired
    private FileEntityWithIdConverter fileEntityWithIdConverter;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private KafkaMessageProducer kafkaMessageProducer;

    /**
     * Handles the logic for processing a file upload.
     * 
     * @param fileUploadRequestDto DTO containing the uploaded file and its
     *                             metadata.
     * @return {@link ResponseEntity} containing the saved file's metadata and ID.
     * @throws Exception if an error occurs during file upload or processing.
     */
    public ResponseEntity<FileEntityWithIdDto> uploadFile(FileUploadRequestDto fileUploadRequestDto) throws Exception {
        // Create the entity.
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(fileUploadRequestDto.getName());
        fileEntity.setExtension(fileUploadRequestDto.getExtension());
        fileEntity.setObjectId(fileUploadRequestDto.getObjectId());
        fileEntity.setSystemId(fileUploadRequestDto.getSystemId());
        fileEntity = fileService.save(fileEntity);

        // Create the base details of the file.
        String filename = fileEntity.getId();
        String directoryPath = fileHelper.getDirectoryPath(fileUploadRequestDto.getSystemId());
        String fullPath = fileHelper.getFullPath(directoryPath, filename,
                fileUploadRequestDto.getExtension());

        // Create the file
        fileHelper.createDirectoryIfNotExists(directoryPath);
        String md5Hash = fileHelper.storeFile(fullPath, fileUploadRequestDto.getFile().getInputStream());

        // Update the entity.
        fileEntity.setHash(md5Hash);
        fileEntity = fileService.save(fileEntity);

        if (kafkaProperties.getKafkaEnabled().equals("true")) {
            String json = objectMapper.writeValueAsString(fileEntity);
            kafkaMessageProducer.sendMessage(KafkaTopics.FILE_SAVED, json);
        }

        // Create the response.
        FileEntityWithIdDto response = fileEntityWithIdConverter.convert(fileEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}