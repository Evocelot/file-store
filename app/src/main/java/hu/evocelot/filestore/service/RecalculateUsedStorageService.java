package hu.evocelot.filestore.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import hu.evocelot.filestore.repository.FileRepository;
import hu.evocelot.filestore.repository.FileStorageLimitRepository;

@Component
public class RecalculateUsedStorageService {

    private static final Logger LOG = LogManager.getLogger(RecalculateUsedStorageService.class);

    private final FileRepository fileRepository;
    private final FileStorageLimitRepository fileStorageLimitRepository;

    public RecalculateUsedStorageService(
            FileRepository fileRepository,
            FileStorageLimitRepository fileStorageLimitRepository) {

        this.fileRepository = fileRepository;
        this.fileStorageLimitRepository = fileStorageLimitRepository;
    }

    @Transactional
    public void recalculateAll() {

        LOG.info("Starting used storage recalculation...");

        List<Object[]> results = fileRepository.findUsedStorageByObjectId();

        for (Object[] result : results) {

            String objectId = (String) result[0];
            long usedDiskSpace = ((Number) result[1]).longValue();

            int updated = fileStorageLimitRepository.updateUsedDiskSpace(
                    objectId,
                    usedDiskSpace);

            if (updated > 0) {
                LOG.info(
                        "Updated used storage. objectId={}, usedDiskSpace={}",
                        objectId,
                        usedDiskSpace);
            } else {
                LOG.warn(
                        "No storage limit found for objectId={}",
                        objectId);
            }
        }

        LOG.info("Used storage recalculation finished.");
    }
}