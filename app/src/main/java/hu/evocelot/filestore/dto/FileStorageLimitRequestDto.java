package hu.evocelot.filestore.dto;

public class FileStorageLimitRequestDto {
    private String objectId;
    private long maxDiskSpace;

    public String getObjectId() {
        return objectId;
    }

    public long getMaxDiskSpace() {
        return maxDiskSpace;
    }
}
