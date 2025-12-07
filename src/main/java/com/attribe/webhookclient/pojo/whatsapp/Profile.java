package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Profile {
	private String name;
	
	
	@Override
	public String toString() {
    	System.out.println(" - name: " + name);
		return super.toString();
	}

}
