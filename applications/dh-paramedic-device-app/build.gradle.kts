import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(project(":common:fhir-model"))
    implementation(project(":common:triton-monitor-sdk"))

    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("com.fazecast:jSerialComm:2.5.2")
    implementation(project(":common:dto"))
    implementation(fileTree("libs"))
    implementation("org.usb4java:usb4java:1.3.0")
    implementation("org.apache.xmlgraphics:batik:1.12")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.12")
    implementation("org.apache.xmlgraphics:batik-codec:1.12")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.document.odt:2.0.2")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.template.velocity:2.0.2")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.odt.odfdom:2.0.2")
    implementation("org.apache.pdfbox:pdfbox:2.0.17")

    testCompile("junit:junit:4.13-rc-1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnit()
}

tasks.bootRun {
    args = listOf("--spring.profiles.active=dev")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.ExperimentalUnsignedTypes")
}
