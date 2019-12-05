plugins {
    java
    id("com.bmuschko.izpack") version "3.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(platform("com.amazonaws:aws-java-sdk-bom:1.11.228"))
        classpath("com.amazonaws:aws-java-sdk-s3")
    }
}

dependencies {
    implementation("org.codehaus.izpack:izpack:5.1.3")
    implementation("org.codehaus.izpack:izpack-compiler:5.1.3")
    implementation("org.codehaus.izpack:izpack-ant:5.1.3")
    implementation("org.codehaus.izpack:izpack-panel:5.1.3")

    izpack("org.codehaus.izpack:izpack:5.1.3")
    izpack("org.codehaus.izpack:izpack-compiler:5.1.3")
    izpack("org.codehaus.izpack:izpack-ant:5.1.3")
    izpack("org.codehaus.izpack:izpack-panel:5.1.3")
}

tasks {

    processResources {
        from("izpack"){ into("$buildDir/izpack") }
    }

    create("copyMainApp", Copy::class) {
        from("../applications/dh-paramedic-device-app/build/libs/dh-paramedic-device-app.jar")
        destinationDir = File("$buildDir/izpack")
    }

    create("copyXml", Copy::class) {
        println("${sourceSets["main"]}")
//        println("${sourceSets["main"].kotlin.asPath}")
        from("${sourceSets["main"].name}/src/izpack") { include("*.xml") }
        destinationDir = File("$buildDir/izpack")
    }

    create("prepareInstaller") {
        dependsOn("shadowJar", ":applications:dh-paramedic-device-app:bootJar", "copyMainApp", "copyXml")

        doLast {


            val izpackDir1 = File("$buildDir/izpack")
/*
            if (izpackDir1.exists()) {
                check(izpackDir1.delete()) { "Unable to delete izpack dir!" }
            }
            check(izpackDir1.mkdirs()) { "Unable to create izpack dir!" }
*/
            val client = com.amazonaws.services.s3.AmazonS3ClientBuilder.standard()
                    .withRegion("ru-central1")
                    .withEndpointConfiguration(com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration("https://storage.yandexcloud.net", "ru-central1"))
                    .build()

            arrayOf(
                    "tves-backend.zip", "tves-driver.zip", "winsw.exe",
                    "zebra-native.zip", "openjdk-11_windows-x64.zip",
                    "info.txt", "paramedic-device-native-service.exe",
                    "SDK_4_15_1_msi.zip"
            ).forEach { fileName ->
                client.getObject(com.amazonaws.services.s3.model.GetObjectRequest("paramedic.distr", fileName), File(izpackDir1, fileName))
            }
        }
    }

    izPackCreateInstaller {
        baseDir = File(buildDir, "izpack")
        dependsOn("prepareInstaller")
    }
}