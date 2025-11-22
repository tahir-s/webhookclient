package com.attribe.webhookclient.pojo.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

	private Long id;
	private String clientId;
	private String status;
	private String whatsAppBusinessAccountId;
	private String phoneNumberId;
}
