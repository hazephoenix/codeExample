package ru.viscur.dh.apps.centralserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@SpringBootApplication
@EnableWebSecurity
class CentralServerApplication : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        // TODO временное решение для закрытия стенда
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .hasRole("ROOT_APP")
                .and()
                .httpBasic()
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        // TODO временное решение для закрытия стенда
        val user = User.withDefaultPasswordEncoder()
                .username("test")
                .password("testGGhdJpldczxcnasw8745")
                .roles("ROOT_APP")
                .build()
        return InMemoryUserDetailsManager(user)
    }
}

fun main(args: Array<String>) {
    runApplication<CentralServerApplication>(*args)
}
