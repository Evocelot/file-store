package hu.evocelot.filestore.controller;

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
import hu.evocelot.filestore.dto.FileUploadRequestDto;
import hu.evocelot.filestore.service.DeleteFileService;
import hu.evocelot.filestore.service.DownloadFileService;
import hu.evocelot.filestore.service.GetFileDetailsService;
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
			DownloadFileService downloadFileService, DeleteFileService deleteFileService) {
		this.uploadFileService = uploadFileService;
		this.getFileDetailsService = getFileDetailsService;
		this.downloadFileService = downloadFileService;
		this.deleteFileService = deleteFileService;
	}

	private UploadFileService uploadFileService;
	private GetFileDetailsService getFileDetailsService;
	private DownloadFileService downloadFileService;
	private DeleteFileService deleteFileService;

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
		return downloadFileService.downloadFile(fileId, checkHash);
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
}
