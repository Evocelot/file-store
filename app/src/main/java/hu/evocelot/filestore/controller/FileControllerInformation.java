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
    public static final String GET_FILE_DETAILS_SUMMARY = "Get file details";

    /**
     * {@value}.
     */
    public static final String GET_FILE_DETAILS_DESCRIPTION = "Endpoint for getting the file details.";

    /**
     * {@value}.
     */
    public static final String GET_FILE_DETAILS_LIST_SUMMARY = "List file details";

    /**
     * {@value}.
     */
    public static final String GET_FILE_DETAILS_LIST_DESCRIPTION = "Endpoint for listing the file details.";

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
    public static final String DELETE_FILE_SUMMARY = "Delete file";

    /**
     * {@value}.
     */
    public static final String DELETE_FILE_DESCRIPTION = "Endpoint for delete a file.";

    /**
     * {@value}.
     */
    public static final String FILE_ID_PARAM_DESCRIPTION = "The id of the file";

    /**
     * {@value}.
     */
    public static final String CHECK_HASH_PARAM_DESCRIPTION = "If set to true, the file's content will be examined during download. If the content has changed, the download will not be allowed.";

    /**
     * {@value}.
     */
    public static final String RECALCULATE_ALL_FILE_SIZES_SUMMARY = "Recalculate all file sizes";

    /**
     * {@value}.
     */
    public static final String RECALCULATE_ALL_FILE_SIZES_DESCRIPTION = "Recalculates the size of all stored files and updates the database in a memory- and CPU-efficient way using paginated processing.";

    /**
     * {@value}.
     */
    public static final String GET_STORAGE_USAGE_SUMMARY = "Get storage usage by objectId";

    /**
     * {@value}.
     */
    public static final String GET_STORAGE_USAGE_DESCRIPTION = "Returns the total used disk space for the given objectId and the maximum allowed disk space (currently fixed to 1 GB).";
}
