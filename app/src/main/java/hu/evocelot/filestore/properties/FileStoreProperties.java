package hu.evocelot.filestore.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Class for reading the relevant project ENVs.
 * 
 * @author mark.danisovszky
 */
@Configuration
@ConfigurationProperties(prefix = "filestore")
public class FileStoreProperties {
    private String storePath;
    private int bufferSize;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
