package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Data;

@Data
public class Context {

    private String from;
    private String id;


    public String toString(){
        System.out.println("Context -->");
    	System.out.println(" - from: " + from);
        System.out.println(" - id: " + id);

        return super.toString();
    }

}
