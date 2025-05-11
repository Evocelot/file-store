package hu.evocelot.filestore.converter;

import org.springframework.stereotype.Component;

import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.model.FileEntity;

/**
 * Converter class that handles conversion between {@link FileEntity} and
 * {@link FileEntityWithIdDto}.
 * 
 * @author mark.danisovszky
 */
@Component
public class FileEntityWithIdConverter implements EntityConverter<FileEntity, FileEntityWithIdDto> {

    public FileEntityWithIdConverter(FileEntityConverter fileEntityConverter) {
        this.fileEntityConverter = fileEntityConverter;
    }

    private FileEntityConverter fileEntityConverter;

    @Override
    public FileEntityWithIdDto convert(FileEntity sourceEntity) {
        FileEntityWithIdDto dto = new FileEntityWithIdDto();

        convert(sourceEntity, dto);

        return dto;
    }

    @Override
    public void convert(FileEntity sourceEntity, FileEntityWithIdDto destinationType) {
        fileEntityConverter.convert(sourceEntity, destinationType);

        destinationType.setId(sourceEntity.getId());
    }

    @Override
    public FileEntity convert(FileEntityWithIdDto sourceType) {
        FileEntity entity = new FileEntity();

        convert(sourceType, entity);

        return entity;
    }

    @Override
    public void convert(FileEntityWithIdDto sourceType, FileEntity destionationEntity) {
        fileEntityConverter.convert(sourceType, destionationEntity);

        destionationEntity.setId(sourceType.getId());
    }
}