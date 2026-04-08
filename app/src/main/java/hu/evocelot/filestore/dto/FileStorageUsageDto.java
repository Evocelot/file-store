package hu.evocelot.filestore.dto;

public class FileStorageUsageDto {

    private long maxDiskSpace;
    private long usedDiskSpace;

    public FileStorageUsageDto(long maxDiskSpace, long usedDiskSpace) {
        this.maxDiskSpace = maxDiskSpace;
        this.usedDiskSpace = usedDiskSpace;
    }

    public long getMaxDiskSpace() {
        return maxDiskSpace;
    }

    public long getUsedDiskSpace() {
        return usedDiskSpace;
    }
}