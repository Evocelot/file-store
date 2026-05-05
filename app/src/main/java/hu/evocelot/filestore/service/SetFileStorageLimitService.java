package hu.evocelot.filestore.service;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import hu.evocelot.filestore.model.FileStorageLimit;
import hu.evocelot.filestore.repository.FileStorageLimitRepository;

@Component
public class SetFileStorageLimitService {

    private final FileStorageLimitRepository repository;

    public SetFileStorageLimitService(FileStorageLimitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void setLimit(String objectId, long maxDiskSpace) {
        FileStorageLimit entity = repository.findByObjectId(objectId);

        if (Objects.isNull(entity)) {
            entity = new FileStorageLimit(objectId);
        }

        entity.setMaxDiskSpace(maxDiskSpace);

        repository.save(entity);
    }
}