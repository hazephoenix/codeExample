dependencies {
    api(project(":service:data-storage-service:data-storage-service-api"))
    implementation("org.springframework:spring-core")
    implementation("com.vladmihalcea:hibernate-types-52")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")

    implementation ("org.postgresql:postgresql")

    compile("javax.validation:validation-api:2.0.1.Final")
}