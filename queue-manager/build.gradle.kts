
plugins {
    id("org.flywaydb.flyway")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.hsqldb:hsqldb")
}