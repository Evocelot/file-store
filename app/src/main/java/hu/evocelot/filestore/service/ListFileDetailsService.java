package hu.evocelot.filestore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import hu.evocelot.filestore.converter.FileEntityWithIdConverter;
import hu.evocelot.filestore.dto.FileEntityWithIdDto;
import hu.evocelot.filestore.dto.PaginatedResponse;
import hu.evocelot.filestore.exception.BaseException;
import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.repository.FileRepository;

/**
 * Service class responsible for retrieving file metadata details in list.
 * <p>
 * This class provides functionality to fetch metadata of a stored files based
 * on its objectId.
 * 
 * @author mark.danisovszky
 */
@Component
public class ListFileDetailsService {

    public ListFileDetailsService(FileRepository fileRepository,
            FileEntityWithIdConverter fileEntityWithIdConverter) {
        this.fileRepository = fileRepository;
        this.fileEntityWithIdConverter = fileEntityWithIdConverter;
    }

    private FileRepository fileRepository;
    private FileEntityWithIdConverter fileEntityWithIdConverter;

    /**
     * Retrieves a paginated list of file metadata, optionally filtered by objectId.
     * <p>
     * If an objectId is provided, only files associated with that identifier are
     * returned.
     * Otherwise, all files are listed. Results are returned in a paginated format.
     * </p>
     *
     * @param objectId optional identifier used to filter files
     * @param pageable pagination and sorting information
     * @return a {@link PaginatedResponse} containing file metadata DTOs
     * @throws BaseException if an error occurs during retrieval
     */
    public PaginatedResponse<FileEntityWithIdDto> list(String objectId, Pageable pageable)
            throws BaseException {
        Specification<FileEntity> spec = null;

        if (StringUtils.isNotBlank(objectId)) {
            spec = filterObjectId(objectId);
        }

        Page<FileEntity> pageResult;
        if (spec == null) {
            pageResult = fileRepository.findAll(pageable);
        } else {
            pageResult = fileRepository.findAll(spec, pageable);
        }

        List<FileEntityWithIdDto> fileDetails = pageResult.getContent()
                .stream().map(file -> {
                    FileEntityWithIdDto dto = fileEntityWithIdConverter.convert(file);
                    return dto;
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                fileDetails,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }

    private Specification<FileEntity> filterObjectId(String objectId) {
        return (root, query, cb) -> cb.equal(root.get("objectId"), objectId);
    }
}
