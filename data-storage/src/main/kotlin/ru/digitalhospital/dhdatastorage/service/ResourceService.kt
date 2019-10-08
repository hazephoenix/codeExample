package ru.digitalhospital.dhdatastorage.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.digitalhospital.dhdatastorage.dto.Resource
import java.math.BigInteger
import java.sql.Timestamp
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

/**
 * Created at 07.10.2019 9:36 by SherbakovaMA
 *
 * Сервис для ресурсов
 */
@Service
class ResourceService(
        private val entityManagerFactory: EntityManagerFactory
) {
    private lateinit var em: EntityManager

    @PostConstruct
    fun init() {
        em = entityManagerFactory.createEntityManager()
    }

    @PreDestroy
    fun destroy() {
        em.close()
    }

    /**
     * Чтение ресурса по id.
     */
    fun byId(resourceType: String, id: String): Any? =
            em.createNativeQuery("select fhirbase_read('$resourceType', '$id')").singleResult

    /**
     * Получение всех ресурсов по типу
     * С возможностью фильтрации по полям. Регистр не учитывается, ищутся подстроки (частичное совпадение)
     * указываем параметр 1го уровня, ищется по 1му и 2му уровню.
     * например, patient: name: [{"given": "Bobby 2","family": "Alexander"}] "name":"NDER"
     * найдется по "name":"ALEX" и "name":"bob", но не найдется по "given":"bob"
     */
    fun all(resourceType: String, requestBody: RequestBodyForResources): List<Resource> {
        val wherePart = makeWherePart(requestBody.filter)
        val orderByPart = " order by " + (requestBody.orderBy?.joinToString(", ") {
            val nameAndOrderType = it.split(" ")
            "r.resource->>'${nameAndOrderType[0]}' " + (if (nameAndOrderType.size > 1) nameAndOrderType[1] else "")
        } ?:"txid")
        val items = em.createNativeQuery(
                "select r.id, r.txid, r.ts, r.resource_type, r.status, r.resource from $resourceType r$wherePart$orderByPart")
                .resultList
        return items.map { item ->
            item as Array<Any>
            Resource(item[0] as String, item[1] as BigInteger, item[2] as Timestamp, item[3] as String, item[4] as String, item[5])
        }
    }

    /**
     * Создание
     * Если указан id у ресурса, то будет создан с таким id. Если не указан, то сгенерится свой
     */
    fun create(resource: String): Any? =
            em.createNativeQuery("select fhirbase_create('$resource'\\:\\:jsonb)").singleResult

    /**
     * Обновление, обязательно вложенное поле id
     */
    fun update(resource: String): Any? =
            em.createNativeQuery("select fhirbase_update('$resource'\\:\\:jsonb)").singleResult

    /**
     * Удаление определенного ресурса по типу [resourceType] и [id]
     * С сохранением истории пред. состояния ресурса
     */
    fun deleteById(resourceType: String, id: String): Any? =
            em.createNativeQuery("select fhirbase_delete('$resourceType', '$id')").singleResult

    /**
     * Удаление всех ресурсов типа [resourceType] по параметрам [RequestBodyForResources.filter].
     * Если параметры не указаны, то удаляются все ресурсы указанного типа.
     * Работает аналогично методу [all]. Удалятся все найденные.
     * Без сохранения истории пред. состояний ресурса
     * @return количество удаленных записей
     */
    @Transactional
    fun deleteAll(resourceType: String, requestBody: RequestBodyForResources): Int {
        val wherePart = makeWherePart(requestBody.filter)
        em.joinTransaction()
        return em.createNativeQuery("delete from $resourceType r$wherePart").executeUpdate()
    }

    private fun makeWherePart(params: Map<String, String>): String =
            if (params.isNotEmpty()) {
                " where " +
                        params.map { (key, value) ->
                            "r.resource ->> '$key' ilike '%$value%'"
                        }.joinToString(" and ")
            } else ""
}