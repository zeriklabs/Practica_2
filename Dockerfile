# Etapa de compilación
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
# Descarga de dependencias
RUN ./mvnw dependency:go-offline -B || true
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx384m", "-Xss512k", "-XX:+UseSerialGC", "-jar", "app.jar"]

