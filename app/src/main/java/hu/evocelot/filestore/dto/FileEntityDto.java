package hu.evocelot.filestore.dto;

import hu.evocelot.filestore.model.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base DTO class for {@link FileEntity}.
 * 
 * @author mark.danisovszky
 */
public class FileEntityDto {

    @Schema(description = "The name of the file (used as filename)", required = true, maxLength = 250)
    private String name;

    @Schema(description = "The extension of the file (e.g., png, jpg, pdf)", required = true, maxLength = 10)
    private String extension;

    @Schema(description = "MD5 hash of the file", required = false, maxLength = 32)
    private String hash;

    @Schema(description = "ID of the connected entity (internal owner of the file)", required = false, maxLength = 100)
    private String objectId;

    @Schema(description = "File creator system ID", required = false, maxLength = 100)
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
