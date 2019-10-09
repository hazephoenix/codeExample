rootProject.name = "DigitalHospital"

include(
        "data-storage",
        "device-service",
        "fhir-model"//,
//        "queue-manager",
//        "service-app"
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
