package hu.evocelot.filestore.dto;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO class for the file upload requests.
 * 
 * @author mark.danisovszky
 */
public class FileUploadRequestDto {

    @Schema(description = "The name of the file (used as filename)", required = true, maxLength = 250)
    private String name;

    @Schema(description = "The extension of the file (e.g., png, jpg, pdf)", required = true, maxLength = 10)
    private String extension;

    @Schema(description = "ID of the connected entity (internal owner of the file)", required = false, maxLength = 100)
    private String objectId;

    @Schema(description = "File creator system ID", required = false, maxLength = 100)
    private String systemId;

    @Schema(description = "The file", required = true)
    private MultipartFile file;

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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
