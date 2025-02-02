package hu.evocelot.filestore.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties class for reading file-store-related properties value.
 * 
 * @author mark.danisovszky
 */
@Component
@ConfigurationProperties(prefix = "filestore")
public class FileStoreProperties {

    private String storePath;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }
}
