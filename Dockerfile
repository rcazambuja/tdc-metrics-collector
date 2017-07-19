FROM jeanblanchard/java:jdk-8

RUN mkdir /app

WORKDIR /app

COPY build/libs/*-fat.jar /app/app.jar

EXPOSE 8080
ENV JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8 -Duser.country=BR -Duser.language=pt -Duser.timezone=America/Sao_Paulo -Djava.net.preferIPv4Stack=true
CMD ["java", "-jar", "-Xmx256M", "-Xms256M", "-XX:MaxMetaspaceSize=128M", "app.jar" ]
