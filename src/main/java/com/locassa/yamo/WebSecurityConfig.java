package com.locassa.yamo;

import com.locassa.yamo.service.YamoUserDetailsService;
import com.locassa.yamo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${yamo.noauth.user.username}")
    private String noAuthUsername;

    @Value("${yamo.noauth.user.password}")
    private String noAuthPassword;

    @Autowired
    private YamoUserDetailsService yamoUserDetailsService;

    @Autowired
    private UserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(noAuthUsername).password(noAuthPassword).roles("USER");
        auth.userDetailsService(yamoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        return new AuthenticationProvider() {
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//                String email = String.valueOf(authentication.getPrincipal());
//                String password = String.valueOf(authentication.getCredentials());
//
//                boolean error = false;
//
//                User existingUser = userService.findUserByEmail(email);
//                if (null == existingUser) {
//                    error = true;
//                }
//
//                if (!error) {
//                    if (!userService.authenticateUser(existingUser.getPassword(), password)) {
//                        error = true;
//                    }
//                }
//
//                if (error) {
//                    throw new BadCredentialsException("Credentials are not valid.");
//                }
//
//                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//                if (UserType.ADMIN.equals(existingUser.getUserType())) {
//                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//                }
//
//                return new UsernamePasswordAuthenticationToken(existingUser.getEmail(), existingUser.getPassword(), authorities);
//            }
//
//            @Override
//            public boolean supports(Class<?> authentication) {
//                return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
//            }
//        };
//    }

}
