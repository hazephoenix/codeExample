//

dependencies {
    api(project(":common:dto"))
    api(project(":common:fhir-model"))
    api(project(":common:transaction-desc"))
    api(project(":service:data-storage-service:data-storage-service-api"))
    api(project(":service:mis-integration-service:mis-integration-service-api"))
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    api("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.2.0.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-artemis:2.2.0.RELEASE")
    implementation(project(":service:mis-integration-service:mis-integration-service-impl"))
}
