package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contact {
	private Profile profile;
    private String wa_id;
    
    @Override
    public String toString() {
    	System.out.println("Contact -->");
    	if(profile!=null) {
    		System.out.println(" - profile" + profile.toString());
    	}
    	System.out.println(" - wa_id: " + wa_id);
    	return super.toString();
    }

}
