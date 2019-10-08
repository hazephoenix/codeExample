
val executableModules = setOf(
        "device-service",
        "service-app"
)

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50" apply false
    kotlin("plugin.jpa") version "1.3.50" apply false
    id("org.flywaydb.flyway") version "5.2.4" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.springframework.boot") version "2.1.9.RELEASE" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    // Плагины для всех подпроектов
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring") // Todo может быть не надо во все проекты добавлять?
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")    // Todo может быть не надо во все проекты добавлять?

    dependencyManagement {
        dependencies {
            dependency("org.springframework:spring-core:4.0.3.RELEASE")
            dependency("com.vladmihalcea:hibernate-types-52:2.7.0")
        }
    }

    // Зависимости для всех подпроектов
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }

    if (this.name !in executableModules) {
        logger.info("Configure Library module: ${this.name}")
        this.tasks.findByName("bootJar")?.enabled = false
        this.tasks.findByName("jar")?.enabled = true
    }

}




dependencies {
    /*implementation("org.springframework.boot:spring-boot-starter-artemis")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.integration:spring-integration-test")
    */
}
