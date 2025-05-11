package hu.evocelot.filestore.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.model.FileEntity;

/**
 * Action class responsible for deleting a file and its metadata.
 * <p>
 * This class provides functionality to remove a stored file and its associated
 * metadata. It interacts with the {@link FileEntityAccessor} to delete the file
 * entity
 * from the database and uses {@link FileHelper} to remove the actual file from
 * the storage system.
 * </p>
 * 
 * @author mark.danisovszky
 */
@Component
public class DeleteFileService {

    public DeleteFileService(FileEntityAccessor fileEntityAccessor, FileHelper fileHelper) {
        this.fileEntityAccessor = fileEntityAccessor;
        this.fileHelper = fileHelper;
    }

    private FileEntityAccessor fileEntityAccessor;
    private FileHelper fileHelper;

    /**
     * Deletes a file and its metadata.
     * <p>
     * This method fetches the file entity by its ID, removes it from the database,
     * and deletes the corresponding file from the storage system.
     * </p>
     * 
     * @param fileId The unique identifier of the file.
     * @return {@link ResponseEntity} with a status indicating the result of the
     *         operation.
     * @throws BaseException If the file is not found.
     */
    public ResponseEntity<Void> deleteFile(String fileId) throws BaseException {
        // Get the file entity.
        Optional<FileEntity> optionalFileEntity = fileEntityAccessor.findById(fileId);
        if (optionalFileEntity.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND, ExceptionType.FILE_ENTITY_NOT_FOUND,
                    "Cannot find file entity with id :" + fileId);
        }
        FileEntity fileEntity = optionalFileEntity.get();

        fileHelper.deleteFile(fileEntity.getSystemId(), fileEntity.getId(), fileEntity.getExtension());
        fileEntityAccessor.delete(fileEntity);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
