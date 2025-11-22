package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
	private String from;
    private String id;
    private String timestamp;
    private Text text;
    private String type;
    
    
    @Override
    public String toString() {
    	System.out.println("Message -->");
    	System.out.println(" - from: " + from);
    	System.out.println(" - id: " + id);
    	System.out.println(" - text: " + text);
    	System.out.println(" - type: " + type);
    	return super.toString();
    }

}
