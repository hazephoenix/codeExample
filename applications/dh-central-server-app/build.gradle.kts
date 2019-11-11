import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {

    api("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-artemis")

    implementation(project(":service:data-storage-service:data-storage-service-api"))
    implementation(project(":service:queue-manager-service:queue-manager-service-api"))
    implementation(project(":service:location-service:location-service-api"))
    implementation(project(":service:mis-integration-service:mis-integration-service-api"))

    runtime(project(":common:transaction-desc"))
    runtime(project(":service:data-storage-service:data-storage-service-impl"))
    runtime(project(":service:queue-manager-service:queue-manager-service-impl"))
    runtime(project(":service:queue-manager-service:queue-manager-service-rest"))
    runtime(project(":service:location-service:location-service-impl"))
    runtime(project(":service:mis-integration-service:mis-integration-service-impl"))
    runtime(project(":service:mis-integration-service:mis-integration-service-rest"))
}

tasks.withType<BootJar>() {
    launchScript()
}
