FROM amazoncorretto:11
#ENV TZ=Europe/Moscow
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java","-jar","/stats-server.jar"]