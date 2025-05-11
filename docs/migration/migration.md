# Migration

## 1.2.0 -> 1.3.0

Regarding the client applications, the file-store path needs to be updated, as the 'file-store' prefix has been added in front of all routes.

Previous URLs:
- http://localhost:8083/swagger-ui/index.html
- http://localhost:8083/actuator/prometheus
- http://localhost:8083/file/upload

New URLs:
- http://localhost:8083/file-store/swagger-ui/index.html
- http://localhost:8083/file-store/actuator/prometheus
- http://localhost:8083/file-store/file/upload
