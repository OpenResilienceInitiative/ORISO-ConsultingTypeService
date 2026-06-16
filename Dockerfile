FROM eclipse-temurin:11-jre
VOLUME ["/tmp","/log"]
EXPOSE 8083
ARG JAR_FILE
ENV JAVA_UPPER_VERSION=eclipse-temurin:11-jre
COPY ./target/ConsultingTypeService.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
