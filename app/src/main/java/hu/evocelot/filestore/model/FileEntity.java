package hu.evocelot.filestore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * The File entity.
 * 
 * @author mark.danisovszky
 */
@Entity
@Table(name = "FILE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FileEntity extends AbstractIdentifiedAuditEntity {

    /**
     * The name of the file (used as filename)
     */
    @Column(name = "name", length = 250, nullable = false)
    @Size(max = 250)
    private String name;

    /**
     * The extension of the file (e.g., png, jpg, pdf)
     */
    @Column(name = "extension", length = 10, nullable = false)
    @Size(max = 10)
    private String extension;

    /**
     * MD5 hash of the file
     */
    @Column(name = "hash", length = 32, nullable = false)
    @Size(max = 32)
    private String hash;

    /**
     * ID of the connected entity (internal owner of the file)
     */
    @Column(name = "object_id", length = 100, nullable = true)
    @Size(max = 100)
    private String objectId;

    /**
     * File creator system ID
     */
    @Column(name = "system_id", length = 100, nullable = true)
    @Size(max = 100)
    private String systemId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
