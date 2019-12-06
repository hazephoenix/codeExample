dependencies {
    implementation(project(":service:practitioner-app-integration-service:practitioner-app-integration-service-api"))
    implementation(project(":service:practitioner-call-service:practitioner-call-service-api"))
    implementation(project(":service:data-storage-service:data-storage-service-api"))
    implementation(project(":common:security"))
    implementation(project(":common:transaction-desc"))

    // TODO сейчас необходима такая зависимость так как бизнес-логику напрямую закодили в
    //      сервсие интеграции для МИС. Как потушим пожар, необходимо вынести БЛ из
    //      сервиса интеграции с МИС
    implementation(project(":service:mis-integration-service:mis-integration-service-api"))

}