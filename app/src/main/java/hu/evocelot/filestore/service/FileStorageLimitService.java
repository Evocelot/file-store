package hu.evocelot.filestore.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.properties.FileStoreProperties;
import hu.evocelot.filestore.repository.FileStorageLimitRepository;

@Service
public class FileStorageLimitService {

    private final FileStorageLimitRepository repository;
    private final FileStoreProperties fileStoreProperties;

    public FileStorageLimitService(FileStorageLimitRepository repository, FileStoreProperties fileStoreProperties) {
        this.repository = repository;
        this.fileStoreProperties = fileStoreProperties;
    }

    @Transactional
    public void reserveStorage(String objectId, long fileSize) throws BaseException {
        if (!fileStoreProperties.isStorageLimitEnabled()) {
            return;
        }

        int updated = repository.reserveStorage(objectId, fileSize);

        if (updated == 0) {
            throw new BaseException(
                    HttpStatus.INSUFFICIENT_STORAGE,
                    ExceptionType.STORAGE_LIMIT_EXCEEDED,
                    "Not enough storage available.");
        }
    }
}