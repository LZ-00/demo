package com.lz.swagger;

import io.swagger.models.auth.In;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

/**
 * @author 乐。
 */
@EnableOpenApi
@Configuration
public class SwaggerConfig {
    
    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket createDocket(){
        return new Docket(DocumentationType.OAS_30).pathMapping("/")
                .enable(swaggerProperties.getEnable())
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                // 接口调试地址
                .host(swaggerProperties.getTryHost())
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lz.controller"))
                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https","http"))
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * 页面上半部分显示的信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getApplicationName()+"Api Doc")
                .description(swaggerProperties.getApplicationDescription())
                .contact(new Contact("lz",null,"123456@qq.com"))
                .version("Application Version: "+swaggerProperties.getApplicationVersion()+", SpringBoot Version: "+ SpringBootVersion.getVersion())
                .build();
    }

    /**
     * 设置授权信息
     * @return
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("token","token 认证", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 设置信息全局应用
     * @return
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("token",new AuthorizationScope[]{new AuthorizationScope("global","")})))
                .build()
        );

    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts ) {
        if(ts.length>0){
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

}
