package com.attribe.webhookclient.service.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.attribe.webhookclient.pojo.client.MessageDTO;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;

@Component("default")
public class DefaultHandler implements ClientHandle {

	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);

	@Autowired
	private WhatsAppSendMessageService messageService;

	@Override
	public void handleInbondMessage(Metadata metadata, Message message) {
		try {
			MessageDTO messageDto = new MessageDTO();
			messageDto.setTo(message.getFrom());
			messageDto.setBody("We will get back to you shortly");

			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			logger.error("Error in DefaultHandler: {}", e.getMessage(), e);
		}
	}

}
