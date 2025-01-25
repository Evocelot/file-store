package hu.evocelot.filestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import hu.evocelot.filestore.model.SampleEntity;
import hu.evocelot.filestore.repository.SampleRepository;

/**
 * Sample service for managing the {@link SampleEntity}.
 * 
 * @author mark.danisovszky
 */
@Service
public class SampleService extends AbstractBaseService<SampleEntity> {

    @Autowired
    private SampleRepository sampleRepository;

    @Override
    protected JpaRepository getRepository() {
        return sampleRepository;
    }
}
