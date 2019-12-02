import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(platform("com.amazonaws:aws-java-sdk-bom:1.11.228"))
        classpath("com.amazonaws:aws-java-sdk-s3")
    }
}

plugins {
    id("com.bmuschko.izpack") version "3.0"
}

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
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.template.freemarker:2.0.2")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.odt.odfdom:2.0.2")
    implementation("org.apache.pdfbox:pdfbox:2.0.17")

    compileOnly("org.codehaus.izpack:izpack:5.1.3")
    compileOnly("org.codehaus.izpack:izpack-ant:5.1.3")
    compileOnly("org.codehaus.izpack:izpack-panel:5.1.3")

    izpack("org.codehaus.izpack:izpack:5.1.3")
    izpack("org.codehaus.izpack:izpack-compiler:5.1.3")
    izpack("org.codehaus.izpack:izpack-ant:5.1.3")
    izpack("org.codehaus.izpack:izpack-panel:5.1.3")

    testCompile("junit:junit:4.13-rc-1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks {
    test {
        useJUnit()
    }
    bootRun {
        args = listOf("--spring.profiles.active=dev")
    }
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.ExperimentalUnsignedTypes")
    }

    create("izpackJars", Jar::class) {

        from("src/main/kotlin/") {
            include("ru/viscur/dh/apps/paramedicdevice/installer/**")
        }
        archiveName = "izpack-add.jar"
    }

    create("prepareInstaller") {
        dependsOn("izpackJars")

        doLast {
            val izpackDir1 = File("$buildDir/izpack")
            if (izpackDir1.exists()) {
                check(izpackDir1.delete()) { "Unable to delete izpack dir!" }
            }
            check(izpackDir1.mkdirs()) { "Unable to create izpack dir!" }
            val client = AmazonS3ClientBuilder.standard()
                    .withRegion("ru-central1")
                    .withEndpointConfiguration(com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration("https://storage.yandexcloud.net", "ru-central1"))
                    .build()

            arrayOf("tves-backend.zip", "tves-driver.zip", "winsw.exe", "zebra-native.zip", "openjdk-11_windows-x64.zip", "info.txt").forEach { fileName ->
                client.getObject(GetObjectRequest("paramedic.distr", fileName), File(izpackDir1, fileName))
            }
        }
    }

    izPackCreateInstaller {
        baseDir = File(buildDir, "izpack")
        dependsOn("bootJar", "prepareInstaller")
    }
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src/main/kotlin")
    kotlin.exclude("ru/viscur/dh/apps/paramedicdevice/installer/**")
}
