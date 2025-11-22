package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Metadata {

	private String display_phone_number;
    private String phone_number_id;
    
    
    @Override
    public String toString() {
    	System.out.println("Metadata -->");
    	System.out.println(" - display_phone_number:" + display_phone_number);
    	System.out.println(" - phone_number_id:" + phone_number_id);
    	return super.toString();
    }
}
