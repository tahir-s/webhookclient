package com.attribe.webhookclient.service.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ClientHandlerFactory {

    @Autowired
     private ApplicationContext context;

     public ClientHandle getHandler(String type) {
        try {
            return context.getBean(type, ClientHandle.class);
        } catch (Exception e) {
            return context.getBean("default", ClientHandle.class);
        }
    }

    



}
