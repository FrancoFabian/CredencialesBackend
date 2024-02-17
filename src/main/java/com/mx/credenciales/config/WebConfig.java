package com.mx.credenciales.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String directorioExternoPath = "file:/Documents/static/";
        registry.addResourceHandler("/fotoPerfil/**").addResourceLocations(directorioExternoPath + "fotoPerfil/");
        registry.addResourceHandler("/fotoFirma/**").addResourceLocations(directorioExternoPath + "fotoFirma/");
        registry.addResourceHandler("/pdf/**").addResourceLocations(directorioExternoPath + "pdf/");
        registry.addResourceHandler("/visualizacion/**").addResourceLocations(directorioExternoPath + "visualizacion/");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

}
