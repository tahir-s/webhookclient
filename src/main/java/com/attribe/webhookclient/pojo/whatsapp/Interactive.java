package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Data;

@Data
public class Interactive {
    private String type; // e.g., button_reply
    private ButtonReply button_reply;

            @Override
            public String toString(){
                System.out.println("Interactive -->");
    	        System.out.println(" - type: " + type);
                
                if(button_reply!=null)
                    button_reply.toString();


                
                return super.toString();
            }

            @Data
            public static class ButtonReply {
        
                private String id;
                private String title;

                 @Override
            public String toString(){
                System.out.println("ButtonReply -->");
    	        System.out.println(" - id: " + id);
                System.out.println(" - title: " + title);
                
                return super.toString();
            }
            }

}
