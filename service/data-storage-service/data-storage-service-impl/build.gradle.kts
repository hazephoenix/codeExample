dependencies {
    api(project(":service:data-storage-service:data-storage-service-api"))
    implementation("org.springframework:spring-core")
    implementation("com.vladmihalcea:hibernate-types-52")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtime ("org.postgresql:postgresql")
}