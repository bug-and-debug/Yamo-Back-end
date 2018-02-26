package com.locassa.yamo;

import com.locassa.yamo.service.YamoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class OAuth2SecurityConfig {

    private static final String RESOURCE_ID = "yamorestservice";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            String[] permittedUrls = {
                    "/css/**",
                    "/fonts/**",
                    "/images/**",
                    "/js/**",
                    "/lang/**",
                    "/lib/**",
                    "/o2c.html",
                    "/swagger-ui.js",
                    "/swagger-ui.min.js",
                    "/login",
                    "/test/**",
                    "/api-docs.html",
                    "/admin.html"
            };

            http.authorizeRequests().antMatchers(permittedUrls).permitAll().anyRequest().fullyAuthenticated();
            http.httpBasic();
            http.csrf().disable();
            http.formLogin()
                    .loginPage("/login").permitAll();
        }

    }

    @Configuration
    @EnableAuthorizationServer
    public static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        @Value("${yamo.oauth.user.username}")
        private String oauthUsername;

        @Value("${yamo.oauth.user.password}")
        private String oauthPassword;

        @Autowired
        private DataSource dataSource;

        private TokenStore tokenStore;

        @PostConstruct
        private void init() {
            this.tokenStore = new JdbcTokenStore(dataSource);
        }

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Autowired
        private YamoUserDetailsService yamoUserDetailsService;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(this.tokenStore).authenticationManager(this.authenticationManager).userDetailsService(this.yamoUserDetailsService);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                    .withClient(oauthUsername)
                    .authorizedGrantTypes("password", "refresh_token")
                    .authorities("USER")
                    .scopes("read", "write")
                    .resourceIds(RESOURCE_ID)
                    .secret(oauthPassword)
            ;
        }

        @Bean
        @Primary
        public DefaultTokenServices defaultTokenServices() {
            DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setSupportRefreshToken(true);
            defaultTokenServices.setTokenStore(this.tokenStore);
            return defaultTokenServices;
        }

        public TokenStore getTokenStore() {
            return tokenStore;
        }

    }

}
