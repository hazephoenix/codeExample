package ru.viscur.dh.datastorage.impl.config.annotation

import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import javax.persistence.PersistenceContext


@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@PersistenceContext(name = PERSISTENCE_UNIT_NAME)
annotation class DataStoragePersistenceContext {
}