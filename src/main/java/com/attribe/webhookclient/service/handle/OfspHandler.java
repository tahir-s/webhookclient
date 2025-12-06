package com.attribe.webhookclient.service.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.attribe.webhookclient.pojo.client.MessageDTO;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;

@Component("OfspHandler")
public class OfspHandler implements  ClientHandle{



	@Autowired
	private WhatsAppSendMessageService messageService;

    


    @Override
    public void handleInbondMessage(Metadata metadata, Message message) {


        /**
		 * Sending message 
		 */
		try {

           
			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("""
                                           Welcom to The Workplace, press *M* for main menu. 
                                           
                                           \ud83d\udd22 *Options*
                                           
                                           1\ufe0f\u20e3 List Sponcered Child
                                           2\ufe0f\u20e3 List Donation
                                           3\ufe0f\u20e3 Get Agent Details
                                           4\ufe0f\u20e3 Support""");
			
		
			String phone_number_id= metadata.getPhone_number_id();
			
            System.out.print(phone_number_id);
			messageService.sendMessage(phone_number_id, messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }

    

}
