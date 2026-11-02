package com.klldh.config;

import com.klldh.interceptor.BaseInterceptor;
import com.klldh.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "NullableProblems"})
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private List<? extends BaseInterceptor> baseInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        baseInterceptors.stream().filter(BaseInterceptor::isEnabled).forEach(interceptor -> registry.addInterceptor(interceptor)
                .addPathPatterns(interceptor.getPathPatterns())
                .excludePathPatterns(interceptor.getInterceptorExcludePathPatterns()));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // ------------------------------------------------------------------------------------------------------------------------

    @Bean
    public ObjectMapper defaultObjectMapper() {
        return JsonHelper.newStandardObjectMapper();
    }

}
