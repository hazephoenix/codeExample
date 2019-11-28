dependencies {
    api(project(":service:queue-manager-service:queue-manager-service-api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-artemis")

    testCompile("junit:junit:4.13-rc-1")
}

tasks.test {
    useJUnit()
}
