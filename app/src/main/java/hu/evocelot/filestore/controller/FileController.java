package hu.evocelot.filestore.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.dto.FileStorageUsageDto;
import hu.evocelot.filestore.dto.FileUploadRequestDto;
import hu.evocelot.filestore.dto.PasswordDto;
import hu.evocelot.filestore.service.DeleteFileService;
import hu.evocelot.filestore.service.DownloadFileService;
import hu.evocelot.filestore.service.GetFileDetailsService;
import hu.evocelot.filestore.service.GetFileStorageUsageService;
import hu.evocelot.filestore.service.ListFileDetailsService;
import hu.evocelot.filestore.service.RecalculateFileSizesService;
import hu.evocelot.filestore.service.UploadFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * REST controller responsible for managing file-related operations.
 * 
 * @author mark.danisovszky
 */
@RestController
@RequestMapping("/file")
public class FileController {

	public FileController(UploadFileService uploadFileService, GetFileDetailsService getFileDetailsService,
			DownloadFileService downloadFileService, DeleteFileService deleteFileService,
			ListFileDetailsService listFileDetailsService, RecalculateFileSizesService recalculateFileSizesService,
			GetFileStorageUsageService getFileStorageUsageService) {
		this.uploadFileService = uploadFileService;
		this.getFileDetailsService = getFileDetailsService;
		this.downloadFileService = downloadFileService;
		this.deleteFileService = deleteFileService;
		this.listFileDetailsService = listFileDetailsService;
		this.recalculateFileSizesService = recalculateFileSizesService;
		this.getFileStorageUsageService = getFileStorageUsageService;
	}

	private final UploadFileService uploadFileService;
	private final GetFileDetailsService getFileDetailsService;
	private final DownloadFileService downloadFileService;
	private final DeleteFileService deleteFileService;
	private final ListFileDetailsService listFileDetailsService;
	private final RecalculateFileSizesService recalculateFileSizesService;
	private final GetFileStorageUsageService getFileStorageUsageService;

	/**
	 * Handles file upload requests.
	 * <p>
	 * This endpoint allows clients to upload a file by sending a multipart
	 * form-data request. The uploaded file's metadata and contents are processed
	 * and stored, returning a response containing details about the uploaded file.
	 * </p>
	 * 
	 * @param fileUploadRequestDto DTO containing file data and associated metadata.
	 * @return {@link ResponseEntity} containing the uploaded file's metadata in the
	 *         body.
	 * @throws Exception If an error occurs during file upload.
	 */
	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = FileControllerInformation.UPLOAD_FILE_SUMMARY, description = FileControllerInformation.UPLOAD_FILE_DESCRIPTION)
	public ResponseEntity<FileEntityWithIdDto> uploadFile(@ModelAttribute FileUploadRequestDto fileUploadRequestDto)
			throws Exception {
		return uploadFileService.uploadFile(fileUploadRequestDto);
	}

	/**
	 * Retrieves metadata details of a file.
	 * <p>
	 * This endpoint allows clients to fetch details about a file by providing its
	 * unique identifier.
	 * </p>
	 * 
	 * @param fileId The unique identifier of the file.
	 * @return {@link ResponseEntity} containing the file's metadata.
	 * @throws Exception If an error occurs while retrieving file details.
	 */
	@GetMapping
	@Operation(summary = FileControllerInformation.GET_FILE_DETAILS_SUMMARY, description = FileControllerInformation.GET_FILE_DETAILS_DESCRIPTION)
	public ResponseEntity<FileEntityWithIdDto> getFileDetails(
			@Parameter(description = FileControllerInformation.FILE_ID_PARAM_DESCRIPTION, required = true) @RequestParam String fileId)
			throws Exception {
		return getFileDetailsService.getFileDetails(fileId);
	}

	/**
	 * Retrieves a paginated list of file metadata associated with a given object.
	 * <p>
	 * This endpoint allows clients to query file metadata records linked to a
	 * specific object identifier. The results are returned in a paginated and
	 * sorted format, ordered by insertion date in descending order.
	 * </p>
	 *
	 * @param page     The zero-based page index to retrieve.
	 * @param size     The number of records per page.
	 * @param objectId The identifier of the related object whose files are queried.
	 * @return {@link ResponseEntity} containing a paginated list of file metadata.
	 * @throws Exception If an error occurs while retrieving the file list.
	 */
	@GetMapping("/list")
	@Operation(summary = FileControllerInformation.GET_FILE_DETAILS_LIST_SUMMARY, description = FileControllerInformation.GET_FILE_DETAILS_LIST_DESCRIPTION)
	public ResponseEntity<?> getFileDetailsList(@RequestParam int page,
			@RequestParam int size,
			@RequestParam String objectId)
			throws Exception {
		Pageable pageable = PageRequest.of(page, size, Sort.by("insDate").descending());

		return ResponseEntity
				.ok(listFileDetailsService.list(objectId, pageable));
	}

	/**
	 * Handles file download requests.
	 * <p>
	 * This endpoint allows clients to download a file by sending the file id.
	 * 
	 * @param fileId    - the id of the file.
	 * @param checkHash - if true, we will check the MD5 hash of the file content.
	 * @return {@link ResponseEntity} containing the downloadable file stream.
	 * @throws Exception when error occurs.
	 */
	@GetMapping("/download")
	@Operation(summary = FileControllerInformation.DOWNLOAD_FILE_SUMMARY, description = FileControllerInformation.DOWNLOAD_FILE_DESCRIPTION)
	public ResponseEntity<StreamingResponseBody> downloadFile(
			@Parameter(description = FileControllerInformation.FILE_ID_PARAM_DESCRIPTION, required = true) @RequestParam String fileId,
			@Parameter(description = FileControllerInformation.CHECK_HASH_PARAM_DESCRIPTION, required = true) @RequestParam boolean checkHash)

			throws Exception {
		return downloadFileService.downloadFile(fileId, checkHash, null);
	}

	/**
	 * Handles file download with password requests.
	 * <p>
	 * This endpoint allows clients to download a file by sending the file id.
	 * 
	 * @param fileId    - the id of the file.
	 * @param checkHash - if true, we will check the MD5 hash of the file content.
	 * @return {@link ResponseEntity} containing the downloadable file stream.
	 * @throws Exception when error occurs.
	 */
	@PostMapping("/download")
	@Operation(summary = FileControllerInformation.DOWNLOAD_SECURE_FILE_SUMMARY, description = FileControllerInformation.DOWNLOAD_SECURE_FILE_DESCRIPTION)
	public ResponseEntity<StreamingResponseBody> downloadProtectedFile(
			@Parameter(description = FileControllerInformation.FILE_ID_PARAM_DESCRIPTION, required = true) @RequestParam String fileId,
			@Parameter(description = FileControllerInformation.CHECK_HASH_PARAM_DESCRIPTION, required = true) @RequestParam boolean checkHash,
			PasswordDto passwordDto)

			throws Exception {
		return downloadFileService.downloadFile(fileId, checkHash, passwordDto);
	}

	/**
	 * Deletes a file and its metadata.
	 * <p>
	 * This endpoint allows clients to delete a file by providing its unique
	 * identifier. The file's metadata and actual content are removed from the
	 * system.
	 * </p>
	 * 
	 * @param fileId The unique identifier of the file.
	 * @return {@link ResponseEntity} with HTTP 204 (No Content) status if deletion
	 *         is successful.
	 * @throws Exception If an error occurs during file deletion.
	 */
	@DeleteMapping
	@Operation(summary = FileControllerInformation.DELETE_FILE_SUMMARY, description = FileControllerInformation.DELETE_FILE_DESCRIPTION)
	public ResponseEntity<Void> deleteFile(
			@Parameter(description = FileControllerInformation.FILE_ID_PARAM_DESCRIPTION, required = true) @RequestParam String fileId)
			throws Exception {
		return deleteFileService.deleteFile(fileId);
	}

	/**
	 * Recalculates the size of all stored files and updates the database.
	 * <p>
	 * This endpoint iterates through all file metadata entries in a paginated
	 * manner, reads the corresponding files from the storage, and recalculates
	 * their sizes in bytes. The recalculated size is then persisted in the
	 * database.
	 * </p>
	 *
	 * <h3>Use cases:</h3>
	 * <ul>
	 * <li>Data migration or backfill after introducing file size column.</li>
	 * <li>Repair inconsistent or missing file size values.</li>
	 * </ul>
	 *
	 * @return {@link ResponseEntity} with HTTP 200 status when the process is
	 *         started/completed.
	 * @throws Exception if an unexpected error occurs during processing.
	 */
	@PostMapping("/recalculate-all-file-sizes")
	@Operation(summary = FileControllerInformation.RECALCULATE_ALL_FILE_SIZES_SUMMARY, description = FileControllerInformation.RECALCULATE_ALL_FILE_SIZES_DESCRIPTION)
	public ResponseEntity<Void> recalculateAllFileSizes() throws Exception {
		recalculateFileSizesService.recalculateAll();
		return ResponseEntity.ok().build();
	}

	/**
	 * Retrieves storage usage information for a given object.
	 * <p>
	 * This endpoint calculates the total size of all files associated with the
	 * given object identifier and returns it יחד with the maximum allowed disk
	 * space.
	 * </p>
	 *
	 * @param objectId The identifier of the related object.
	 * @return {@link ResponseEntity} containing storage usage information.
	 */
	@GetMapping("/storage-usage")
	@Operation(summary = FileControllerInformation.GET_STORAGE_USAGE_SUMMARY, description = FileControllerInformation.GET_STORAGE_USAGE_DESCRIPTION)
	public ResponseEntity<FileStorageUsageDto> getStorageUsage(@RequestParam String objectId) {
		return ResponseEntity.ok(getFileStorageUsageService.getUsage(objectId));
	}
}
