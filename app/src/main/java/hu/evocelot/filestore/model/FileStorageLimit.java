package hu.evocelot.filestore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * The file storage limit entity.
 * 
 * @author mark.danisovszky
 */
@Entity
@Table(name = "FILE_STORAGE_LIMIT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FileStorageLimit extends AbstractIdentifiedAuditEntity {

    public FileStorageLimit() {

    }

    public FileStorageLimit(String objectId) {
        this.objectId = objectId;
    }

    /**
     * ID of the connected entity (internal owner of the file)
     */
    @Column(name = "object_id", length = 100, nullable = true)
    @Size(max = 100)
    private String objectId;

    /**
     * Max disk space
     */
    @Column(name = "MAX_DISK_SPACE", nullable = true)
    private Long maxDiskSpace;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Long getMaxDiskSpace() {
        return maxDiskSpace;
    }

    public void setMaxDiskSpace(Long maxDiskSpace) {
        this.maxDiskSpace = maxDiskSpace;
    }

}
