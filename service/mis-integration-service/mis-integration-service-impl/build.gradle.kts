dependencies {
    api(project(":service:mis-integration-service:mis-integration-service-api"))
    api(project(":service:data-storage-service:data-storage-service-api"))
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    api(project(":common:fhir-model"))
    api(project(":common:dto"))
    api(project(":common:transaction-desc"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.2.0.RELEASE")
}

springBoot {
    mainClassName = "ru.viscur.dh.integration.mis.impl.MisIntegrationAppKt"
}
