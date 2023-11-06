package com.SPB.Bank77.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ComponentScan
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private UserDetailsService myUserDetailsService;
	@Autowired
    private UserDetailsService adminDetailsService;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(adminDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .userDetailsService(myUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

	
	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	 http
         .authorizeRequests()
         .antMatchers("/admin/**").hasRole("ADMIN") // Protect admin-related endpoints
         .antMatchers("/user/**").hasRole("USER") // Protect user-related endpoints
         .anyRequest().authenticated()
         .and()
             .formLogin()
             /*.loginProcessingUrl("/admin/login")*/
             .failureUrl("/loginerror.html")
             
             .defaultSuccessUrl("/admin/dashboard") // Redirect to the "welcome.html" page
             
             .permitAll()
             .and()
         .logout()
             .logoutUrl("/logout")
             .logoutSuccessUrl("/")
             .permitAll()
             .and()
         .exceptionHandling().accessDeniedPage("/access-denied");
    }
 
}
