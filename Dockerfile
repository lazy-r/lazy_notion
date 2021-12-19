FROM java:8
COPY target/*.jar /app.jar
CMD ["--server.port=6666"]
EXPOSE  6666
ENTRYPOINT ["java","-jar","/app.jar"]
