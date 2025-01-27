package hu.evocelot.filestore.converter;

import org.springframework.stereotype.Component;

import hu.evocelot.filestore.dto.FileEntityDto;
import hu.evocelot.filestore.model.FileEntity;

/**
 * Converter class that handles conversion between {@link FileEntity} and
 * {@link FileEntityDto}.
 * 
 * @author mark.danisovszky
 */
@Component
public class FileEntityConverter implements EntityConverter<FileEntity, FileEntityDto> {

    @Override
    public FileEntity convert(FileEntityDto sourceType) {
        FileEntity entity = new FileEntity();

        convert(sourceType, entity);

        return entity;
    }

    @Override
    public void convert(FileEntityDto sourceType, FileEntity destionationEntity) {
        destionationEntity.setName(sourceType.getName());
        destionationEntity.setExtension(sourceType.getExtension());
        destionationEntity.setHash(sourceType.getHash());
        destionationEntity.setObjectId(sourceType.getObjectId());
        destionationEntity.setSystemId(sourceType.getSystemId());
    }

    @Override
    public FileEntityDto convert(FileEntity sourceEntity) {
        FileEntityDto dto = new FileEntityDto();

        convert(sourceEntity, dto);

        return dto;
    }

    @Override
    public void convert(FileEntity sourceEntity, FileEntityDto destinationType) {
        destinationType.setName(sourceEntity.getName());
        destinationType.setExtension(sourceEntity.getExtension());
        destinationType.setHash(sourceEntity.getHash());
        destinationType.setObjectId(sourceEntity.getObjectId());
        destinationType.setSystemId(sourceEntity.getSystemId());
    }
}
