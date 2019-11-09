import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.net.URI
import java.nio.file.Paths

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.bmuschko:gradle-izpack-plugin:3.0")
        classpath(platform("software.amazon.awssdk:bom:2.10.12"))
        classpath("software.amazon.awssdk:s3")
        classpath("org.codehaus.izpack:izpack-dist:5.1.3")
    }
}

plugins {
    id("com.bmuschko.izpack") version "3.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fazecast:jSerialComm:2.5.2")
    implementation(project(":common:fhir-model"))
    implementation(fileTree("libs"))
    implementation("org.usb4java:usb4java:1.3.0")

    testCompile("junit:junit:4.13-rc-1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    izpack("org.codehaus.izpack:izpack-dist:5.1.3")
}


tasks {
    test {
        useJUnit()
    }

    bootRun {
        args = listOf("--spring.profiles.active=dev")
    }

    izPackCreateInstaller {
//        dependsOn("clean", "bootJar")
//        dependsOn("bootJar")
        dependsOn("buildInstaller")
    }
}

task("buildInstaller") {
    doFirst {
        val yaURI = URI("https://storage.yandexcloud.net")
        val bucket = "paramedic.distr"
        val files = listOf("zebra-native.zip", "tves-backend.zip", "tves-driver.zip", "winsw.exe")
        val izpackDir = File("$buildDir/assemble/izpack")
        if (izpackDir.exists()) {
            check(izpackDir.delete()) {"Unable to delete izpackDir"}
        } else {
            check(izpackDir.mkdirs()) {"Unable to create izpackDir on ${izpackDir.path}"}
        }

        val s3 = S3Client.builder().endpointOverride(yaURI).build()
        files.forEach {
            s3.getObject(GetObjectRequest.builder().bucket(bucket).key(it).build(), ResponseTransformer.toFile(Paths.get("${izpackDir.absolutePath}/$it")))
        }
    }
}
