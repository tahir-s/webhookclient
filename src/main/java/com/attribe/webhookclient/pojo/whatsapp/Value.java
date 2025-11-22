package com.attribe.webhookclient.pojo.whatsapp;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Value {

	private String messaging_product;
    private Metadata metadata;
    private List<Contact> contacts;
    private List<Message> messages;
    
    
    
    @Override
    public String toString() {
    	System.out.println("Value -->");
    	System.out.println(" - messaging_product:" + messaging_product);
    	
    	System.out.println(" - metadata:");
    	if(metadata!=null) {
    		metadata.toString();
    	}
    	
    	
    	System.out.println(" - contacts:");
    	if(contacts !=null) {
    		for(Contact contact : contacts) {
    			contact.toString();
    		}    	
    	}
    
    	
    	System.out.println(" - messages:");
    	if(contacts !=null) {
    		for(Message message : messages) {
    			message.toString();
    		}    	
    	}
    	return super.toString();
    }
    
    
 
}
