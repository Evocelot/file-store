package hu.evocelot.filestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.evocelot.filestore.model.FileEntity;

/**
 * File repository for defining the custom functions for the
 * {@link FileEntity}.
 * 
 * @author mark.danisovszky
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {

}
