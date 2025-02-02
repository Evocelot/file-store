package hu.evocelot.filestore.action;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import hu.evocelot.filestore.converter.FileEntityWithIdConverter;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.service.FileService;

/**
 * Action class responsible for retrieving file metadata details.
 * <p>
 * This class provides functionality to fetch metadata of a stored file based on
 * its unique identifier. It interacts with the {@link FileService} to fetch the
 * file entity and converts it into a DTO representation using
 * {@link FileEntityWithIdConverter}.
 * </p>
 * 
 * @author mark.danisovszky
 */
@Component
public class GetFileDetailsAction {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntityWithIdConverter fileEntityWithIdConverter;

    /**
     * Retrieves metadata details of a file.
     * <p>
     * This method fetches the file entity by its ID, converts it into a DTO, and
     * returns the details as an HTTP response.
     * </p>
     * 
     * @param fileId The unique identifier of the file.
     * @return {@link ResponseEntity} containing the file's metadata.
     * @throws BaseException If the file is not found.
     */
    public ResponseEntity<FileEntityWithIdDto> getFileDetails(String fileId)
            throws BaseException {
        // Get the file entity.
        Optional<FileEntity> optionalFileEntity = fileService.findById(fileId);
        if (optionalFileEntity.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND, ExceptionType.FILE_ENTITY_NOT_FOUND,
                    "Cannot find file entity with id :" + fileId);
        }
        FileEntity fileEntity = optionalFileEntity.get();

        // Create the response.
        FileEntityWithIdDto response = fileEntityWithIdConverter.convert(fileEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
