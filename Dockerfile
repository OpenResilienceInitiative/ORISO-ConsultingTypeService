FROM eclipse-temurin:11-jre
VOLUME ["/tmp","/log"]
EXPOSE 8080
ARG JAR_FILE
ENV JAVA_UPPER_VERSION=eclipse-temurin:21-jre
COPY ./ConsultingTypeService.jar app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
