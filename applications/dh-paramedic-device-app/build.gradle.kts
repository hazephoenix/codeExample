dependencies {
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fazecast:jSerialComm:2.5.2")
    implementation(project(":common:fhir-model"))
    implementation(project(":common:dto"))
    implementation(fileTree("libs"))
    implementation("org.usb4java:usb4java:1.3.0")

    testCompile("junit:junit:4.13-rc-1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnit()
}

tasks.bootRun {
    args = listOf("--spring.profiles.active=dev")
}
