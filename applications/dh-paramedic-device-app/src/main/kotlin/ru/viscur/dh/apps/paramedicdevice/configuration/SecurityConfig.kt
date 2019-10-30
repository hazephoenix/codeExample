package ru.viscur.dh.apps.paramedicdevice.configuration

import io.netty.util.CharsetUtil.encoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Created at 29.10.2019 19:04 by TimochkinEA
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
        private val uid: AppUID
): WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser(uid.uid).password(encoder().encode(uid.apiPassword)).roles("API")
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
    }

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}
