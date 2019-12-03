import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    api(project(":service:location-service:location-service-api"))
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation(project(":common:dto"))
    implementation(fileTree("libs"))
    testCompile("junit:junit:4.13-rc-1")
    implementation("info.picocli:picocli:4.0.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.ExperimentalUnsignedTypes")
    dependsOn(tasks.processResources)
}
