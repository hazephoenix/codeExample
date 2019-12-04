package ru.viscur.dh.practitioner.call.impl.config

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["ru.viscur.dh.practitioner.call.impl"])
@AutoConfigureAfter(
        name = ["ru.viscur.dh.datastorage.impl.config.DataStorageConfig"]
)
class PractitionerCallConfig {
}