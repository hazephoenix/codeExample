package ru.viscur.dh.datastorage.api

import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.digitalhospital.dhdatastorage.dto.Resource
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Сервис для ресурсов
 */
interface ResourceService {

    /**
     * Чтение ресурса по id
     *
     * @param resourceType тип ресурса
     * @param id идентификатор ресурса
     */
    fun <T> byId(resourceType: ResourceType<T>, id: String): T?
            where T : BaseResource

    /**
     * Получение всех ресурсов по типу с возможностью фильтрации по полям.
     *
     * Регистр не учитывается, ищутся подстроки (частичное совпадение)
     * указываем параметр 1го уровня, ищется по 1му и 2му уровню.
     * например, patient: name: [{"given": "Bobby 2","family": "Alexander"}] "name":"NDER"
     * найдется по "name":"ALEX" и "name":"bob", но не найдется по "given":"bob"
     *
     * @param resourceType тип ресурса
     * @param requestBody запрос на поиск
     */
    fun <T> all(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): List<Resource<T>>
            where T : BaseResource

    /**
     * Создание
     * Если указан id у ресурса, то будет создан с таким id. Если не указан, то сгенерится свой
     *
     * TODO: точно можно присылать ID при создании???
     */
    fun <T> create(resource: T): T?
            where T : BaseResource

    /**
     * Обновление, обязательно вложенное поле id
     */
    fun <T> update(resource: T): T?
            where T : BaseResource

    /**
     * Удаление определенного ресурса по типу [resourceType] и [id]
     * С сохранением истории пред. состояния ресурса
     */
    fun <T> deleteById(resourceType: ResourceType<T>, id: String): T?
            where T : BaseResource

    /**
     * Удаление всех ресурсов типа [resourceType] по параметрам [RequestBodyForResources.filter].
     * Если параметры не указаны, то удаляются все ресурсы указанного типа.
     * Работает аналогично методу [all]. Удалятся все найденные.
     * Без сохранения истории пред. состояний ресурса
     * @return количество удаленных записей
     */
    fun <T> deleteAll(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): Int
            where T : BaseResource


}