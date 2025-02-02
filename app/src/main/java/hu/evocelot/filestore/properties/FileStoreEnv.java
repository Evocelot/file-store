package hu.evocelot.filestore.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for reading the relevant project ENVs.
 * 
 * @author mark.danisovszky
 */
@Component
public class FileStoreEnv {
    @Value("${BUFFER_SIZE:}")
    private int bufferSize;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
