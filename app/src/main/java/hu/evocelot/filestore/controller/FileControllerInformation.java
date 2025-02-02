package hu.evocelot.filestore.controller;

/**
 * Class for storing informations about the FileController endpoints.
 * 
 * @author mark.danisovszky
 */
public class FileControllerInformation {

    /**
     * {@value}.
     */
    public static final String UPLOAD_FILE_SUMMARY = "Upload file";

    /**
     * {@value}.
     */
    public static final String UPLOAD_FILE_DESCRIPTION = "Endpoint for uploading a file.";

    /**
     * {@value}.
     */
    public static final String DOWNLOAD_FILE_SUMMARY = "Download file";

    /**
     * {@value}.
     */
    public static final String DOWNLOAD_FILE_DESCRIPTION = "Endpoint for downloading a file.";

    /**
     * {@value}.
     */
    public static final String FILE_ID_PARAM_DESCRIPTION = "The id of the file";

    /**
     * {@value}.
     */
    public static final String CHECK_HASH_PARAM_DESCRIPTION = "If set to true, the file's content will be examined during download. If the content has changed, the download will not be allowed.";
}
