package io.github.orionlibs.orion_websocket_tools;

import jakarta.servlet.ServletRegistration.Dynamic;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return new Class<?>[] {WebSecurityConfig.class};
    }


    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return new Class<?>[] {WebConfig.class, WebSocketConfiguration.class, WebSocketSecurityConfig.class};
    }


    @Override
    protected String[] getServletMappings()
    {
        return new String[] {"/"};
    }


    @Override
    protected void customizeRegistration(Dynamic registration)
    {
        registration.setInitParameter("dispatchOptionsRequest", "true");
    }
}
