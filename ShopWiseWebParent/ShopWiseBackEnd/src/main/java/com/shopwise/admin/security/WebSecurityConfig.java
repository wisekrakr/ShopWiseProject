package com.shopwise.admin.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserAuthenticationService();
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(PasswordEncoder());

        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/**").hasAuthority("Admin")
                .antMatchers("/categories/**","/brands/**")
                    .hasAnyAuthority("Admin","Editor")
                .antMatchers("/products/new", "/products/delete/**")
                    .hasAnyAuthority("Admin", "Editor")
                .antMatchers("/products/edit/**", "/products/save", "/products/check_uniqueness")
                    .hasAnyAuthority("Admin", "Editor", "Sales")
                .antMatchers("/products", "/products/", "/products/details/**", "/products/page/**")
                    .hasAnyAuthority("Admin","Editor","Sales","Shipper")
                .antMatchers("/products/**")
                    .hasAnyAuthority("Admin","Editor")
                .anyRequest()
                .authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
                .rememberMe()
                .key(RandomStringUtils.randomAlphabetic(30))
                .tokenValiditySeconds(24 * 60 * 60);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**", "/richtext/**");
    }


}
