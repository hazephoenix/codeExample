//

dependencies {
    api(project(":common:fhir-model"))
    api(project(":common:transaction-desc"))
    api(project(":service:data-storage-service:data-storage-service-api"))
    api(project(":service:mis-integration-service:mis-integration-service-api"))
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    api("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.2.0.RELEASE")
}