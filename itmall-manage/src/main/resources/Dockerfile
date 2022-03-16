FROM java:8
VOLUME /tmp
ADD ./itmall-manage-1.0-SNAPSHOT.jar /app.jar
EXPOSE 8502
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
