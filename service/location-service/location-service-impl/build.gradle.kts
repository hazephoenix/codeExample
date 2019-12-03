dependencies {
    api(project(":service:location-service:location-service-api"))
    api(project(":service:data-storage-service:data-storage-service-api"))
    api(project(":service:data-storage-service:data-storage-service-impl"))
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.apache.activemq:artemis-jms-server")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
