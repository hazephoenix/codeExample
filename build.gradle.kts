import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Проверка, является ли проект собираемым. По умолчанию, собираем только проекты,
 * у которых нет подпроектов
 */
fun isBuildableProject(project: Project) = project.childProjects.isEmpty()

/**
 * Проверка, является ли проект исполняемым. По умолчанию, все проекты, которые находится в applications
 * считаются исполняемыми
 */
fun isExecutableProject(project: Project) = project.path.startsWith(":applications:")

/**
 * Подключать ли автоматически SpringBoot
 */
fun isApplySpringBoot(project: Project): Boolean {
    if (project.path.startsWith(":common:")) {
        // Если какому-то модулю в common нужен boot, подключаем в самом модуле
        return false;
    }
    if (project.path.endsWith("-api")) {
        // В api не нужен boot (бесполезная или даже вредная зависимость)
        return false;
    }
    return true
}

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50" apply false
    kotlin("plugin.jpa") version "1.3.50" apply false
    id("org.flywaydb.flyway") version "5.2.4" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.springframework.boot") version "2.2.0.BUILD-SNAPSHOT" apply false // TODO заменить как появится релиз
}


allprojects {
    repositories {
        mavenCentral()
        // TODO убрать milestone и snapshot как релизнится spring boot 2.2.0
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
    if (isBuildableProject(this)) {
        tasks.withType<KotlinCompile> {
            kotlinOptions.suppressWarnings = true
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        }
        tasks.withType<Test> {
            useJUnitPlatform();
        }
    } else {
        tasks.forEach {
            if (it.name != "clean") {
                it.enabled = false
            }
        }
    }
}

subprojects {
    if (isBuildableProject(this)) {
        val applyBoot = isApplySpringBoot(this)
        // Плагины для всех подпроектов
        apply(plugin = "java")
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "io.spring.dependency-management")
        if (applyBoot) {
            apply(plugin = "org.springframework.boot")
        }
        apply(plugin = "org.jetbrains.kotlin.plugin.spring") // Todo может быть не надо во все проекты добавлять?
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")    // Todo может быть не надо во все проекты добавлять?

        dependencyManagement {
            dependencies {
                //dependency("org.springframework:spring-core:5.2.0.RELEASE")
                dependency("com.vladmihalcea:hibernate-types-52:2.7.0")
            }
        }

        // Зависимости для всех подпроектов
        dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")

            if (applyBoot) {
                implementation("org.springframework.boot:spring-boot-starter")
                testImplementation("org.springframework.boot:spring-boot-starter-test") {
                    //            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
                }
                annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
            }
        }
        if (isExecutableProject(this)) {
            println("Configure executable project: ${this.path}")
        } else {
            println("Configure library project: ${this.path}")
            this.tasks.findByName("bootJar")?.enabled = false
            this.tasks.findByName("jar")?.enabled = true
        }
    }
}