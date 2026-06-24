# Runtime: Java 11 (eclipse-temurin:11-jre). See pom.xml:java.version and CTS-H01 for upgrade track.
FROM eclipse-temurin:11-jre
VOLUME ["/tmp","/log"]
EXPOSE 8083
ARG JAR_FILE
COPY ./target/ConsultingTypeService.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
