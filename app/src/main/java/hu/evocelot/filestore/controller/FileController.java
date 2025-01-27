package hu.evocelot.filestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.evocelot.filestore.action.UploadFileAction;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.dto.FileUploadRequestDto;
import io.swagger.v3.oas.annotations.Operation;

/**
 * REST controller responsible for managing file-related operations.
 * 
 * @author mark.danisovszky
 */
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private UploadFileAction uploadFileAction;

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
		return uploadFileAction.uploadFile(fileUploadRequestDto);
	}
}
