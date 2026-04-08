package hu.evocelot.filestore.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import hu.evocelot.filestore.accessor.FileEntityAccessor;
import hu.evocelot.filestore.helper.FileHelper;
import hu.evocelot.filestore.model.FileEntity;
import hu.evocelot.filestore.repository.FileRepository;

@Component
public class RecalculateFileSizesService {
    private static final Logger LOG = LogManager.getLogger(RecalculateFileSizesService.class);

    private final FileEntityAccessor fileEntityAccessor;
    private final FileRepository fileRepository;
    private final FileHelper fileHelper;

    private static final int PAGE_SIZE = 100;

    public RecalculateFileSizesService(FileEntityAccessor fileEntityAccessor,
            FileHelper fileHelper, FileRepository fileRepository) {
        this.fileEntityAccessor = fileEntityAccessor;
        this.fileRepository = fileRepository;
        this.fileHelper = fileHelper;
    }

    public void recalculateAll() throws Exception {
        LOG.info("Starting file size recalculation...");

        int page = 0;
        Page<FileEntity> result;

        do {
            result = fileRepository.findAll(PageRequest.of(page, PAGE_SIZE));
            List<FileEntity> files = result.getContent();

            LOG.info("Processing page {} with {} files", page, files.size());

            for (FileEntity file : files) {
                recalculateSingle(file);
            }

            page++;
        } while (!result.isLast());

        LOG.info("File size recalculation finished.");
    }

    private void recalculateSingle(FileEntity file) {
        try {
            String directoryPath = fileHelper.getDirectoryPath(file.getSystemId());
            String fullPath = fileHelper.getFullPath(
                    directoryPath,
                    file.getId(),
                    file.getExtension());

            Path path = Paths.get(fullPath);

            if (Files.exists(path)) {
                long size = Files.size(path);

                if (file.getSize() == null || !file.getSize().equals(size)) {
                    LOG.debug("Updating size for fileId={} oldSize={} newSize={}",
                            file.getId(), file.getSize(), size);

                    file.setSize(size);
                    fileEntityAccessor.save(file);
                } else {
                    LOG.debug("Size unchanged for fileId={}", file.getId());
                }
            } else {
                LOG.warn("File not found on disk. fileId={}, path={}",
                        file.getId(), fullPath);
            }

        } catch (Exception e) {
            LOG.error("Error recalculating size for fileId={}. Reason: {}",
                    file.getId(), e.getMessage(), e);
        }
    }
}
