package hu.evocelot.filestore.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.repository.FileStorageLimitRepository;

@Service
public class FileStorageLimitService {

    private final FileStorageLimitRepository repository;

    public FileStorageLimitService(FileStorageLimitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void reserveStorage(String objectId, long fileSize) throws BaseException {

        int updated = repository.reserveStorage(objectId, fileSize);

        if (updated == 0) {
            throw new BaseException(
                    HttpStatus.INSUFFICIENT_STORAGE,
                    ExceptionType.STORAGE_LIMIT_EXCEEDED,
                    "Not enough storage available.");
        }
    }
}