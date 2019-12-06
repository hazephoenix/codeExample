package ru.viscur.dh.datastorage.api.exception

class EntityNotFoundException(val id: Any) :
        RuntimeException("Entity with id = '$id' doesn't exist") {
}