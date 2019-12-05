package ru.viscur.dh.practitioner.call.impl.config

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerService
import ru.viscur.dh.practitioner.call.impl.loudspeaker.impl.AlwaysPlayLoudspeakerService
import ru.viscur.dh.practitioner.call.impl.loudspeaker.impl.LocalLoudspeakerSystem
import ru.viscur.dh.practitioner.call.impl.loudspeaker.impl.NopLoudspeakerService

@Configuration
@ComponentScan(basePackages = ["ru.viscur.dh.practitioner.call.impl"])
@AutoConfigureAfter(
        name = ["ru.viscur.dh.datastorage.impl.config.DataStorageConfig"]
)
class PractitionerCallConfig {


    @Bean
    fun loudspeakerService(): LoudspeakerService = NopLoudspeakerService()

    /*@Bean
    fun loudspeakerService(): LoudspeakerService =
            AlwaysPlayLoudspeakerService(
                    LocalLoudspeakerSystem()
            )*/
}