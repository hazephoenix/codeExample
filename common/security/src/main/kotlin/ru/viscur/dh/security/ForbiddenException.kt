package ru.viscur.dh.security

import java.lang.RuntimeException

class ForbiddenException : RuntimeException("Forbidden") {
}