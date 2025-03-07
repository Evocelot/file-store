include local.env
export $(shell sed 's/=.*//' local.env)

NETWORK_NAME=evocelot-network
IMAGE_NAME=$(APPNAME):$(VERSION)
LOGSTASH_HOST=logstash
LOGSTASH_PORT=5000
JAEGER_HOST=jaeger
JAEGER_TRACING_PORT=4317

# Wrapper command to run a command and indent its output with a tab for better readability.
define run-with-output-indent
	@$(1) | sed 's/^/\t/'
endef

# Creates podman network.
create-podman-network:
	@echo "Creating podman network"
	$(call run-with-output-indent,\
		podman network create $(NETWORK_NAME) 2>/dev/null || \
		echo "There is no need to create the network named $(NETWORK_NAME) because it already exists.")

# Deletes the created podman network.
delete-podman-network:
	@echo "Deleting podman network"
	$(call run-with-output-indent,\
		podman network rm $(NETWORK_NAME) 2>/dev/null || \
		echo "The network named $(NETWORK_NAME) cannot be deleted.")

# Builds the application and a docker image.
build-docker-image:
	@echo "Building the application"
	$(call run-with-output-indent,\
		./gradlew bootJar)

	@echo "Building the image"
	$(call run-with-output-indent,\
		podman build -t $(IMAGE_NAME) \
		--build-arg JAR_FILE=/app/build/libs/$(APPNAME)-$(VERSION).jar .)

# Runs the local container.
start-local-container: create-podman-network stop-local-container build-docker-image
	@echo "Starting the local podman container"
	$(call run-with-output-indent,\
		podman run \
		--name $(APPNAME) \
		--network $(NETWORK_NAME) \
		-p 8082:8080 \
		-e TZ=UTC \
		-e LOGSTASH_ENABLED="true" \
		-e LOGSTASH_HOST=$(LOGSTASH_HOST) \
		-e LOGSTASH_PORT=$(LOGSTASH_PORT) \
		-e TRACING_ENABLED="true" \
		-e TRACING_URL=http://$(JAEGER_HOST):$(JAEGER_TRACING_PORT)/v1/traces \
		-e KAFKA_ENABLED="true" \
		-e KAFKA_URL=evocelot-kafka:9092 \
		-e KAFKA_GROUP_ID=file-group \
		-e SPRING_DATASOURCE_URL=jdbc:mariadb://evocelot-mariadb:3306/filestore \
		-e SPRING_DATASOURCE_USERNAME=root \
		-e SPRING_DATASOURCE_PASSWORD=admin \
		-e SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver \
		-e BUFFER_SIZE=8192 \
		-v ./stored-files:/store \
  		--cpus="0.5" \
  		--memory="512m" \
  		--memory-swap="512m" \
		$(IMAGE_NAME); \
		\
		echo "$(APPNAME) started at: http://localhost:8082\nThe swagger UI can be accessed at: http://localhost:8082/swagger-ui/index.html")

# Stops the local container.
stop-local-container:
	@echo "Stopping the local podman container"
	$(call run-with-output-indent,\
		podman rm -f $(APPNAME))

# Starts ELK stack.
start-elk-stack: create-podman-network stop-elk-stack
	@echo "Starting Elasticsearch"
	$(call run-with-output-indent,\
		podman run -d \
		--name elasticsearch \
		--network $(NETWORK_NAME) \
		-p 9200:9200 \
		-p 9300:9300 \
		-e TZ=UTC \
		-e "discovery.type=single-node" \
		-e "xpack.security.enabled=false" \
		-e "xpack.security.http.ssl.enabled=false" \
  		--cpus="0.8" \
  		--memory="2048m" \
  		--memory-swap="2048m" \
		elasticsearch:8.17.0; \
		\
		echo "elasticsearch can be accessed at: http://localhost:9200")

	@echo "Starting Logstash"
	$(call run-with-output-indent,\
		podman run -d \
		--name $(LOGSTASH_HOST) \
		--network $(NETWORK_NAME) \
		-p $(LOGSTASH_PORT):5000 \
		-p 5044:5044 \
		-p 9600:9600 \
		-e TZ=UTC \
		-v ./elk/logstash/pipeline:/usr/share/logstash/pipeline \
  		--cpus="0.5" \
  		--memory="2048m" \
  		--memory-swap="2048m" \
		logstash:8.16.2; \
		\
		echo "Logstash can be accessed at: http://localhost:5000")

	@echo "Starting Kibana"
	$(call run-with-output-indent,\
		podman run -d \
		--name kibana \
		--network $(NETWORK_NAME) \
		-p 5601:5601 \
		-e TZ=UTC \
		kibana:8.17.0; \
		\
		echo "Kibana started at: http://localhost:5601")

# Stops ELK stack.
stop-elk-stack:
	@echo "Stopping ELK stack"
	$(call run-with-output-indent,\
		podman rm -f kibana logstash elasticsearch)

# Starts the observability tools.
start-observability: create-podman-network stop-observability
	@echo "Starting Jaeger"
	$(call run-with-output-indent,\
		podman run -d \
		--name $(JAEGER_HOST) \
		--network $(NETWORK_NAME) \
		-p 16686:16686 \
		-p $(JAEGER_TRACING_PORT):4317 \
		-e TZ=UTC \
		-e COLLECTOR_OTLP_ENABLED=true \
  		--cpus="0.5" \
  		--memory="64m" \
  		--memory-swap="64m" \
		jaegertracing/all-in-one:1.64.0; \
		\
		echo "Jaeger can be accessed at: http://localhost:16686")

	@echo "Starting Prometheus"
	$(call run-with-output-indent,\
		podman run -d \
		--name prometheus \
		--network $(NETWORK_NAME) \
		-p 9090:9090 \
		-e TZ=UTC \
		-v ./observability/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml \
  		--cpus="0.2" \
  		--memory="64m" \
  		--memory-swap="64m" \
		prom/prometheus:v3.0.1; \
		\
		echo "Prometheus can be accessed at: http://localhost:9090")

	@echo "Starting Grafana"
	$(call run-with-output-indent,\
		podman run -d \
		--name grafana \
		--network $(NETWORK_NAME) \
		-p 3000:3000 \
		-e TZ=UTC \
		-e "GF_SECURITY_ADMIN_PASSWORD=admin" \
		-v ./observability/grafana/provisioning:/etc/grafana/provisioning \
  		--cpus="0.2" \
  		--memory="128m" \
  		--memory-swap="128m" \
		grafana/grafana:11.4.0; \
		\
		echo "Grafana can be accessed at: http://localhost:3000")

stop-observability:
	@echo "Stopping Observability tools"
	$(call run-with-output-indent,\
		podman rm -f jaeger prometheus grafana)


# Starts local kafka container.
start-kafka: stop-kafka
	@echo "Starting zookeeper container"
	$(call run-with-output-indent,\
		podman run -d \
			--name evocelot-zookeeper \
			--network $(NETWORK_NAME) \
			-e TZ=UTC \
			-e ZOOKEEPER_CLIENT_PORT=2181 \
			-e ZOOKEEPER_TICK_TIME=2000 \
			-p 2181:2181 \
			confluentinc/cp-zookeeper:7.9.0; \
		\
		echo "Zookeeper can be accessed at: http://localhost:2181")

	@echo "Starting kafka container"
	$(call run-with-output-indent,\
		podman run -d \
			--name evocelot-kafka \
			--network $(NETWORK_NAME) \
			-e TZ=UTC \
			-e KAFKA_BROKER_ID=1 \
			-e KAFKA_ZOOKEEPER_CONNECT=evocelot-zookeeper:2181 \
			-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://evocelot-kafka:9092 \
			-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
			-e KAFKA_LOG_DIRS=/var/lib/kafka/data \
			-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
			-p 9092:9092 \
			confluentinc/cp-kafka:7.9.0; \
		\
		echo "Kafka can be accessed at: http://localhost:9092")

	@echo "Starting kafka ui container"
	$(call run-with-output-indent,\
		podman run -d \
			--name evocelot-kafka-ui \
			--network $(NETWORK_NAME) \
			-e TZ=UTC \
			-e DYNAMIC_CONFIG_ENABLED=true \
			-p 9093:8080 \
			provectuslabs/kafka-ui:v0.7.2; \
		\
		echo "Kafka ui can be accessed at: http://localhost:9093")

# Stops kafka containers.
stop-kafka:
	@echo "Stopping Kafka tools"
	$(call run-with-output-indent,\
		podman rm -f evocelot-zookeeper evocelot-kafka evocelot-kafka-ui)

all: create-podman-network build-docker-image start-elk-stack start-observability start-kafka start-local-container

stop-all: stop-elk-stack stop-observability stop-local-container
