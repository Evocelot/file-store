package hu.evocelot.filestore.pojo;

public class FileStoreResult {
    private String hash;
    private long size;

    public FileStoreResult(String hash, long size) {
        this.hash = hash;
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public long getSize() {
        return size;
    }
}