FROM maven:latest as build
WORKDIR /app

COPY /pom.xml /app
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY /src /app/src
RUN mvn clean package -DskipTests -Dnet.bytebuddy.experimental=true

FROM amazoncorretto:20

COPY --chown=185 --from=build /app/target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 --from=build /app/target/quarkus-app/*.jar /deployments/
COPY --chown=185 --from=build /app/target/quarkus-app/app/ /deployments/app/
COPY --chown=185 --from=build /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE $PORT
USER root

ENV AB_JOLOKIA_OFF=""

ENV JAVA_VERSION=20
ENV PORT="3000"
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=3000 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

CMD ["java", "-Xmx2048m", "-Dquarkus.http.port=3000", "-jar", "/deployments/quarkus-run.jar"]