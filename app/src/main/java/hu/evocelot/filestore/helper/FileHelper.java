package hu.evocelot.filestore.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.exception.ExceptionType;
import hu.evocelot.filestore.properties.FileStoreEnv;
import hu.evocelot.filestore.properties.FileStoreProperties;
import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Utility class for handling file storage operations, such as saving files,
 * building file paths, and creating directories.
 * 
 * @author mark.danisovszky
 */
@Component
public class FileHelper {

    private static final Logger LOG = LogManager.getLogger(FileHelper.class);
    private static final String MD5_DIGEST = "MD5";

    @Autowired
    private FileStoreProperties fileStoreProperties;

    @Autowired
    private FileStoreEnv fileStoreEnv;

    /**
     * Stores a file at the specified path using the provided input stream.
     *
     * @param fullPath    the full path where the file will be saved.
     * @param inputStream the input stream containing the file data.
     * @return the MD5 hash of the file content.
     * @throws Exception if the input parameters are invalid or if an error occurs
     *                   during file saving.
     */
    public String storeFile(String fullPath, InputStream inputStream) throws Exception {
        if (StringUtils.isBlank(fullPath) || inputStream == null) {
            throw new BaseException(HttpStatus.BAD_REQUEST, ExceptionType.INVALID_INPUT,
                    "Invalid file path or input stream.");
        }

        MessageDigest messageDigest = MessageDigest.getInstance(MD5_DIGEST);

        try (DigestInputStream DigestInputStream = new DigestInputStream(inputStream, messageDigest);
                FileOutputStream fileOutputStream = new FileOutputStream(fullPath);) {

            byte[] buffer = new byte[fileStoreEnv.getBufferSize()];
            int bytesRead;
            while ((bytesRead = DigestInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            LOG.info("File successfully saved at: " + fullPath);
        } catch (IOException e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionType.CANNOT_SAVE_FILE, e.getMessage());
        }

        byte[] hashBytes = messageDigest.digest();
        return (new HexBinaryAdapter()).marshal(hashBytes);
    }

    /**
     * Constructs a directory path based on the system ID and the base storage path.
     *
     * @param systemId the unique identifier for the system, can be null or blank.
     * @return the constructed directory path.
     */
    public String getDirectoryPath(String systemId) {
        StringBuilder directoryPathBuilder = new StringBuilder();

        directoryPathBuilder.append(fileStoreProperties.getStorePath()).append(File.separatorChar);

        if (StringUtils.isNotBlank(systemId)) {
            directoryPathBuilder.append(systemId).append(File.separatorChar);
        }

        return directoryPathBuilder.toString();
    }

    /**
     * Constructs the full file path by combining the directory path, file name, and
     * extension.
     *
     * @param directoryPath the directory path.
     * @param filename      the name of the file.
     * @param extension     the file extension (without the dot).
     * @return the constructed full file path.
     */
    public String getFullPath(String directoryPath, String filename, String extension) {
        StringBuilder fullPathBuilder = new StringBuilder();

        fullPathBuilder.append(directoryPath).append(filename).append(".").append(extension);

        return fullPathBuilder.toString();
    }

    /**
     * Creates a directory at the specified path if it does not already exist.
     *
     * @param directoryPath the directory path to create.
     * @throws RuntimeException if the directory could not be created.
     */
    public void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                LOG.info("Created new directory: " + directoryPath);
            } else {
                throw new RuntimeException("Could not create directory at: " + directoryPath);
            }
        }
    }
}
