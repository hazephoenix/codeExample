//

dependencies {
    api(project(":common:fhir-model"))
    api(project(":service:data-storage-service:data-storage-service-api"))
    api(project(":service:data-storage-service:data-storage-service-impl"))
    api(project(":service:mis-integration-service:mis-integration-service-api"))
    api("org.springframework.boot:spring-boot-starter-web")
}