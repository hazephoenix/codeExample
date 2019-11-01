package ru.viscur.dh.transaction.desc.config.annotation

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Transactional(value = "dsTxManager")
annotation class Tx(
        /**
         * @see [Transactional.propagation]
         */
        val propagation: Propagation = Propagation.REQUIRED,

        /**
         * @see [Transactional.readOnly]
         */
        val readOnly: Boolean = false
)