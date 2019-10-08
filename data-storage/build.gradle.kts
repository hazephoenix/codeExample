plugins {
    id("org.flywaydb.flyway")
}

dependencies {
    api(project(":fhir-model"))
    implementation("org.springframework:spring-core")
    implementation("com.vladmihalcea:hibernate-types-52")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}




tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
