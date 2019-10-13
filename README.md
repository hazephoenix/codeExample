# Цифровой госпиталь

## Модули

* applications - проекты исполняемых приложений. Для всех подпроектов, запускается task bootJar (Spring Boot), для сборки исполняемого jar файла. 
Для остальных (не подпроектов applications) происходит вызов стандартного task jar (собирается как библиотека)  
    * dh-central-server - центральный, монолитный сервер  
    * dh-paramedic-device - приложение для стола фельдшера (управляет оборудованием)
    * dh-rfid-location-device-app - приложение управляющее rfid-устройствами (TODO под вопросом)
* common - общие библиотеки/компоненты 
    * fhir-model  - общая модель данных (в формате FHIR)
    * spring-boot - самописные вещи для Spring Boot
        *  flyway-module-configuration - позволяет запускать локальные миграции Flyway на базе данных подсистемы (например сервиса)  
* service - набор сервисов
    * data-storage-service - сервис хранилища данных
        * data-storage-service-api - API сервиса
        * data-storage-service-impl - реализация сервиса
    * location-service - сервис местоположения специалистов
        * location-service-api - API сервиса
        * location-service-impl - реализация сервиса
    * mis-integration-service - сервис интеграции с МИС
        * mis-integration-service-api - API сервиса 
        * mis-integration-service-impl - реализация сервиса
    * queue-manager-service - сервис управления очередью пациентов
        * queue-manager-service-api - API сервиса
        * queue-manager-service-impl - реализация сервиса
        * queue-manager-service-rest - Rest API сервиса
        * queue-manager-service-rest-cli - rest клиент сервиса