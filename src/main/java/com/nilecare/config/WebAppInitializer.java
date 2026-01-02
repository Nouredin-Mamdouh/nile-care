package com.nilecare.config;

import org.springframework.lang.NonNull;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;
import java.util.Collections;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class}; // Loads Database/Services
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class}; // Loads Controllers/Views
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"}; // Map all requests to DispatcherServlet
    }

    @Override
    protected Filter[] getServletFilters() {
        // Register Spring Security filter chain
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy("springSecurityFilterChain");
        return new Filter[] { filterProxy };
    }

    @Override
    public void onStartup(@NonNull ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        
        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
    }
}