FROM eclipse-temurin:21-jre-noble@sha256:977602aad3cf25b74bbb4c8e7532060eef80e1f1288932da35f5fbecd6159b65
VOLUME ["/tmp","/log"]
EXPOSE 8083
ARG JAR_FILE
COPY ./target/ConsultingTypeService.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
