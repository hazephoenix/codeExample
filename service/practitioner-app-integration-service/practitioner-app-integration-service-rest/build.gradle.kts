dependencies {
    implementation(project(":service:practitioner-app-integration-service:practitioner-app-integration-service-api"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation(project(":common:security"))

}