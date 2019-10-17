package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

@Service
class ObservationServiceImpl(
        private val resourceService: ResourceService
) : ObservationService {

    /**
     * Зарегистрировать обследование (результат еще не получен)
     */
    override fun create(observation: Observation): Observation? {
        return resourceService.create(observation)
    }

    /**
     * Обновить обследование (добавить результаты) и
     * соответствующее направление, если обследование завершено
     *
     * Обследование обязательно должно содержать поле basedOn
     * со ссылкой на ServiceRequest TODO: валидация запроса? сортировка оставшихся обследований?
     */
    override fun update(observation: Observation): Observation? {
        if (observation.status == ObservationStatus.final) {
            resourceService.byId(ResourceType.ServiceRequest, observation.basedOn?.id!!)
                    .let {
                        it.status = ServiceRequestStatus.completed
                        resourceService.update(it)
                    }
        }
        return resourceService.update(observation)
    }
}