package com.attribe.webhookclient.pojo.whatsapp;

import java.util.List;

import org.springframework.lang.Contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class WebhookPayload {
	private String object;
    private List<Entry> entry;

    
    @Override
    public String toString() {
    	System.out.println("WebhookPayload ------------------------------- Start");
    	System.out.println("object: "+ object);
    	if(entry != null) {
    		for (Entry oEntry : entry) {
    			System.out.println("id:" + oEntry.getId()); // Entry is consist of id & changes list
    			for (Change change : oEntry.getChanges()) { // Change is field of files & value object
    				System.out.println(" -- field: "+ change.getField());
	    			Value value = change.getValue();
		    			
	    			if(value!=null) {
	    				value.toString();
	    				
	    			}
    			}
    			
    		}
    	}
    	System.out.println("WebhookPayload ------------------------------- End");
    	return super.toString();
    	
    }
    
}
