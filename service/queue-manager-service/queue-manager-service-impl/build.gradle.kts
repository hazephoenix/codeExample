dependencies {
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation(project(":service:practitioner-app-integration-service:practitioner-app-integration-service-api"))
    implementation(project(":service:practitioner-call-service:practitioner-call-service-api"))

    testCompile("junit:junit:4.13-rc-1")
}

tasks.test {
    useJUnit()
}
