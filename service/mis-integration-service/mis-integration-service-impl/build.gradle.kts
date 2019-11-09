dependencies {
    api(project(":service:mis-integration-service:mis-integration-service-api"))
    api(project(":common:fhir-model"))
    api(project(":common:dto"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-artemis")
}

springBoot {
    mainClassName = "ru.viscur.dh.integration.mis.impl.MisIntegrationAppKt"
}
