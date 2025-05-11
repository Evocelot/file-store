package hu.evocelot.filestore.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.model.FileEntity;

/**
 * Handles the action of downloading a file from the file store.
 * <p>
 * This class is responsible for retrieving file metadata and serving the file
 * as a downloadable stream to the client.
 * </p>
 * 
 * @author mark.danisovszky
 */
@Component
public class DownloadFileService {

    private static final Logger LOG = LogManager.getLogger(DownloadFileService.class);

    public DownloadFileService(FileEntityAccessor fileEntityAccessor, FileHelper fileHelper) {
        this.fileEntityAccessor = fileEntityAccessor;
        this.fileHelper = fileHelper;
    }

    private FileEntityAccessor fileEntityAccessor;
    private FileHelper fileHelper;

    /**
     * Downloads a file based on its unique identifier.
     * <p>
     * This method retrieves file metadata from the database, constructs the file's
     * path, verifies its existence and readability, and streams the file to the
     * client as an HTTP response.
     * </p>
     *
     * @param fileId    the unique identifier of the file to download
     * @param checkHash if true, we will check the MD5 hash of the file content.
     * 
     * @return a {@link ResponseEntity} containing the file as a
     *         {@link StreamingResponseBody}
     * @throws Exception when error occurs.
     */
    public ResponseEntity<StreamingResponseBody> downloadFile(String fileId, boolean checkHash) throws Exception {
        // Get the file entity.
        Optional<FileEntity> optionalFileEntity = fileEntityAccessor.findById(fileId);
        if (optionalFileEntity.isEmpty()) {
            throw new BaseException(HttpStatus.NOT_FOUND, ExceptionType.FILE_ENTITY_NOT_FOUND,
                    "Cannot find file entity with id :" + fileId);
        }

        FileEntity fileEntity = optionalFileEntity.get();
        String directoryPath = fileHelper.getDirectoryPath(fileEntity.getSystemId());
        String fullPath = fileHelper.getFullPath(directoryPath, fileId, fileEntity.getExtension());

        // Get the file.
        File file = new File(fullPath);
        if (!file.exists() || !file.canRead()) {
            throw new BaseException(HttpStatus.NOT_FOUND,
                    ExceptionType.FILE_ENTITY_NOT_FOUND,
                    "Cannot find file in path:" + fullPath);
        }

        // Check the file content hash if needed.
        if (checkHash) {
            String actualHash = fileHelper.getFileHash(fullPath);
            if (!actualHash.equals(fileEntity.getHash())) {
                throw new BaseException(HttpStatus.CONFLICT, ExceptionType.CORRUPTED_FILE,
                        "Corrupted file!");
            }
        }

        // Streaming the file as a response.
        StreamingResponseBody responseBody = outputStream -> {
            try {
                fileHelper.getFile(fullPath, outputStream);
            } catch (Exception e) {
                LOG.error("Error while streaming file", e);
                throw new IOException(e);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileEntity.getName() + "." + fileEntity.getExtension() + "\"")
                .contentLength(file.length())
                .body(responseBody);
    }
}