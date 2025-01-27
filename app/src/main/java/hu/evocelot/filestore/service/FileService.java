package hu.evocelot.filestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.repository.FileRepository;

/**
 * File service for managing the {@link FileEntity}.
 * 
 * @author mark.danisovszky
 */
@Service
public class FileService extends AbstractBaseService<FileEntity> {

    @Autowired
    private FileRepository fileRepository;

    @Override
    protected JpaRepository getRepository() {
        return fileRepository;
    }
}
