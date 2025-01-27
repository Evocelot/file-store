package hu.evocelot.filestore.dto;

import hu.evocelot.filestore.model.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO class for {@link FileEntity} base details with id.
 * 
 * @author mark.danisovszky
 */
public class FileEntityWithIdDto extends FileEntityDto {
    @Schema(description = "Unique identifier of the sample entity", required = true)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
