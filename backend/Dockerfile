# ── Build stage ──────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Cache dependencies separately from source so code edits don't
# force a full re-download on every image build.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src ./src
RUN ./mvnw -B -ntp clean package -DskipTests

# ── Runtime stage ────────────────────────────────────────────
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Run as a non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring
COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring

EXPOSE 8080

# Plain TCP check — no actuator dependency in the project yet.
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=5 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080' || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]