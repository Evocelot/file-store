package hu.evocelot.filestore.exception;

/**
 * Enum representing the various types of exceptions that can occur in the
 * application.
 * <p>
 * Each value corresponds to a specific error scenario that may be thrown as an
 * exception.
 * </p>
 * 
 * @author mark.danisovszky
 */
public enum ExceptionType {
    /**
     * {@value}.
     */
    REQUEST_BODY_CANNOT_BE_BLANK,

    /**
     * {@value}.
     */
    FILE_ENTITY_NOT_FOUND,

    /**
     * {@value}.
     */
    CANNOT_SAVE_FILE,

    /**
     * {@value}.
     */
    INVALID_INPUT,

    /**
     * {@value}.
     */
    CANNOT_READ_FILE,

    /**
     * {@value}.
     */
    CORRUPTED_FILE,

    /**
     * {@value}.
     */
    CANNOT_CALCULATE_MD5,
}
