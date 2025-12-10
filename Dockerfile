# 1️⃣ Base image
FROM eclipse-temurin:21-jdk

# 2️⃣ Working directory
WORKDIR /app

# 3️⃣ Build-time argument
ARG JAR_FILE

# 4️⃣ Spring profil (runtime ENV)
ENV SPRING_PROFILES_ACTIVE=dev

# 5️⃣ Copy JAR from build context
COPY ${JAR_FILE} app.jar

# 6️⃣ Expose port
EXPOSE 8080

# 7️⃣ Run the JAR
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
