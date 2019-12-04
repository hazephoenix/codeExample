package ru.viscur.dh.integration.practitioner.app.rest.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.integration.practitioner.app.rest.security.MisAuthenticationProvider

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan(basePackages = ["ru.viscur.dh.integration.practitioner.app.rest"])
class PractitionerAppRestConfig {


    @Configuration
    @Order(1)
    class ApiWebSecurityConfigurationAdapter(
            val resourceService: ResourceService,
            val conceptService: ConceptService
    ) :
            WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http
                    .antMatcher("/integration/practitioner-app/**")
                    .csrf().disable()
                    .authenticationProvider(
                            MisAuthenticationProvider(
                                    resourceService,
                                    conceptService
                            )
                    )
                    .authorizeRequests()
                    .anyRequest()
                    .hasRole("MIS_PRACTITIONER")
            http.httpBasic {
                it.realmName("PractitionerAppApi")
            }
        }
    }

}