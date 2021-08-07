package com.shopwise.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class used to serve static file resources
 * By default, this handler serves static content from any of /static, /public, /resources,
 * and /META-INF/resources directories that are on the classpath
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    //to expose the directory to access for the class

    /**
     * ResourceHandlerRegistry to configure ResourceHttpRequestHandlers for serving static resources from the classpath,
     * the WAR, or the file system. We can configure the ResourceHandlerRegistry programmatically inside our web context
     * configuration class.
     * Create a route to save files.
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        addResourceHandler("user-avatars",registry);
        addResourceHandler("./ShopWiseWebParent/category-images",registry);
        addResourceHandler("./ShopWiseWebParent/brand-logos",registry);
        addResourceHandler("./ShopWiseWebParent/product-images",registry);
        addResourceHandler("./ShopWiseWebParent/site-logo",registry);
    }

    private void addResourceHandler(String dirName,ResourceHandlerRegistry registry){
        Path dir = Paths.get(dirName);
        String path = dir.toFile().getAbsolutePath();

        String finalPath = dirName.replace("./ShopWiseWebParent/","") + "/**";

        registry.addResourceHandler(finalPath).addResourceLocations("file:/" + path + "/");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("index");
//        registry.addViewController("/login").setViewName("login");
//
//    }
}
