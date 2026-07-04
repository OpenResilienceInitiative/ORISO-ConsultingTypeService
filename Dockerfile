FROM eclipse-temurin:21-jre
VOLUME ["/tmp","/log"]
EXPOSE 8083
ARG JAR_FILE
COPY ./target/ConsultingTypeService.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
