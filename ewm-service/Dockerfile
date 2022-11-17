FROM amazoncorretto:11
#ENV TZ=Europe/Moscow
COPY target/*.jar ewm-service.jar
ENTRYPOINT ["java","-jar","/ewm-service.jar"]