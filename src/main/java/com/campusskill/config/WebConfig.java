package com.campusskill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final List<String> SPA_ROUTES = List.of(
            "/", "/login", "/signup", "/dashboard", "/profile", "/tutors", "/requests",
            "/groups", "/groups/new", "/groups/{id}", "/admin"
    );

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/public/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (String route : SPA_ROUTES) {
            registry.addViewController(route).setViewName("forward:/index.html");
        }
    }
}
