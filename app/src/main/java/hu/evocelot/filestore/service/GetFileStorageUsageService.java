package hu.evocelot.filestore.service;

import org.springframework.stereotype.Component;

import hu.evocelot.filestore.dto.FileStorageUsageDto;
import hu.evocelot.filestore.repository.FileRepository;

/**
 * Service responsible for calculating storage usage for files belonging to a
 * specific object.
 * <p>
 * It aggregates the total used disk space from the database and returns it
 * together with a predefined maximum storage limit.
 * </p>
 */
@Component
public class GetFileStorageUsageService {

    private final FileRepository fileRepository;

    private static final long MAX_DISK_SPACE = 1024L * 1024L * 1024L; // 1 GB

    public GetFileStorageUsageService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Returns storage usage information for the given object.
     *
     * @param objectId the identifier of the object whose file storage usage is
     *                 calculated
     * @return a {@link FileStorageUsageDto} containing used and maximum disk space
     */
    public FileStorageUsageDto getUsage(String objectId) {
        Long used = fileRepository.sumSizeByObjectId(objectId);
        long usedDiskSpace = used != null ? used : 0L;

        return new FileStorageUsageDto(MAX_DISK_SPACE, usedDiskSpace);
    }
}