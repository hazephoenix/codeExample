
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fazecast:jSerialComm:2.5.2")
    implementation(project(":common:fhir-model"))
    testCompile("junit:junit:4.13-rc-1")
}

tasks.test {
    useJUnit()
}

tasks.bootRun {
    args = listOf("--spring.profiles.active=dev")
}
