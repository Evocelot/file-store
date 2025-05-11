package hu.evocelot.filestore.accessor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.repository.FileRepository;

/**
 * File entity accessor for managing the {@link FileEntity}.
 * 
 * @author mark.danisovszky
 */
@Service
public class FileEntityAccessor extends AbstractEntityAccessor<FileEntity> {

    public FileEntityAccessor(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    private FileRepository fileRepository;

    @Override
    protected JpaRepository getRepository() {
        return fileRepository;
    }
}
