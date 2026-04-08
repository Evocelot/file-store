package hu.evocelot.filestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.evocelot.filestore.model.FileEntity;

/**
 * File repository for defining the custom functions for the
 * {@link FileEntity}.
 * 
 * @author mark.danisovszky
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, String>, JpaSpecificationExecutor<FileEntity> {
    @Query("SELECT COALESCE(SUM(f.size), 0) FROM FileEntity f WHERE f.objectId = :objectId")
    Long sumSizeByObjectId(@Param("objectId") String objectId);
}
