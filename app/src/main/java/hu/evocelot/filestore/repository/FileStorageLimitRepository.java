package hu.evocelot.filestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hu.evocelot.filestore.model.FileStorageLimit;

/**
 * File repository for defining the custom functions for the
 * {@link FileStorageLimit}.
 * 
 * @author mark.danisovszky
 */
@Repository
public interface FileStorageLimitRepository extends JpaRepository<FileStorageLimit, String> {
  @Query("select f.maxDiskSpace from FileStorageLimit f where f.objectId = :objectId")
  Long findMaxDiskSpaceByObjectId(String objectId);

  @Query("select f from FileStorageLimit f where f.objectId = :objectId")
  FileStorageLimit findByObjectId(String objectId);

  @Modifying
  @Query("""
          UPDATE FileStorageLimit f
          SET f.usedDiskSpace = f.usedDiskSpace + :fileSize
          WHERE f.objectId = :objectId
            AND f.usedDiskSpace + :fileSize <= f.maxDiskSpace
      """)
  int reserveStorage(String objectId, long fileSize);

  @Modifying
  @Query("""
          UPDATE FileStorageLimit f
          SET f.usedDiskSpace = :usedDiskSpace
          WHERE f.objectId = :objectId
      """)
  int updateUsedDiskSpace(String objectId, long usedDiskSpace);
}
