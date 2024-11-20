package com.project.shopapp.configuration;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
//@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/products/uploads/**", apiPrefix))
                        .permitAll()

                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/roles", apiPrefix),
                                String.format("%s/categories**", apiPrefix),
                                String.format("%s/categories/**", apiPrefix),
                                String.format("%s/products**", apiPrefix),
                                String.format("%s/products/**", apiPrefix),
                                String.format("%s/orders/**", apiPrefix),
                                String.format("%s/products/images/**", apiPrefix))
                        .permitAll()

                         //Admin
//                        .requestMatchers(HttpMethod.GET,
//                                String.format("%s/orders/get-orders-by-keyword", apiPrefix)).hasAnyRole(Role.ADMIN)

                         //Category
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        //Product
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/products**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/products**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/products**", apiPrefix)).hasAnyRole(Role.ADMIN)

                        //Order

                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)

                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)

                        //Order detail
                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)

                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)

                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)

                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                        .anyRequest().authenticated();
                })
            .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();

    }
}
