# ---- Stage 1: Build ----
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

RUN ./gradlew installDist

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/build/install /app/build/install

ENV PORT=8080
EXPOSE 8080

CMD ["/app/build/install/ecomm/bin/ecomm"]
