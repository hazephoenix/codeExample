plugins {
    id("org.flywaydb.flyway")
}

dependencies {
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.hsqldb:hsqldb")
}
