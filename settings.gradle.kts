rootProject.name = "DigitalHospital"

include(
        "common:spring-boot:flyway-module-configuration",
        "common:fhir-model",
        "service:data-storage-service",
        "service:data-storage-service:data-storage-service-api",
        "service:data-storage-service:data-storage-service-impl",
        "service:queue-manager-service:queue-manager-service-api",
        "service:queue-manager-service:queue-manager-service-impl",
        "service:queue-manager-service:queue-manager-service-rest",
        "service:queue-manager-service:queue-manager-service-rest-cli",
        "service:mis-integration-service",
        "service:mis-integration-service:mis-integration-service-api",
        "service:mis-integration-service:mis-integration-service-impl",
        "service:location-service:location-service-api",
        "service:location-service:location-service-impl",
        "applications:dh-central-server-app",
        "applications:dh-paramedic-device-app"
        /*TODO "applications:dh-rfid-location-device-app"*/
)

pluginManagement {
    repositories {
        // TODO убрать milestone и snapshot как релизнится spring boot 2.2.0
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
        }
    }
}
