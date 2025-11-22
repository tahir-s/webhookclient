package com.attribe.webhookclient.pojo.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {
	
	private String to;
	private String messagingProduct = "whatsapp";
	private String recipientType = "individual";
	private String type = "text";
	private String previewUrl = "false";
	private String body;
	
	

}
