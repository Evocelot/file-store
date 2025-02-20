package hu.evocelot.filestore.kafka;

/**
 * Utility class for defining constants related to Kafka topics.
 * <p>
 * This class contains the keys for various Kafka topics used within the
 * application. Kafka topics are used to organize messages within a Kafka
 * broker, and the keys defined here are used to specify the topic when sending
 * or consuming messages.
 * </p>
 * 
 * @author mark.danisovszky
 */
public class KafkaTopics {
    /**
     * The Kafka topic key for notifying that a file has been saved.
     * <p>
     * This topic is used by related modules to trigger actions that need to
     * be performed after a file has been successfully saved.
     * </p>
     */
    public static final String FILE_SAVED = "file-saved";
}
