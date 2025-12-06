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


       
		try {
            String commandRecived = message.getText() != null ? message.getText().getBody() : "";


            switch (commandRecived) {
            case "1": // List Sponcered Child
                sendListSponceredChildMessage(metadata, message);
                break;
            case "2": // List Donation
                sendListDonationessage(metadata, message);
                break;
            case "3": // Get Latest News
                sendListLatestNewsMessage(metadata, message);
                break;
            default:
                System.out.println("Unknown type!");
                sendShowMenuMessage(metadata, message);
        }

           
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }
;
    }



    /**
     * This method will send menu to the customer
     */
    private void sendShowMenuMessage(Metadata metadata, Message message) {


		try {

           
			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("""
                                           Welcom to The Workplace, press *M* for main menu. 
                                           
                                           \ud83d\udd22 *Options*
                                           
                                           1\ufe0f\u20e3 List Sponcered Child
                                           2\ufe0f\u20e3 List Donation
                                           3\ufe0f\u20e3 Get Latest development""");
			
		
			String phone_number_id= metadata.getPhone_number_id();
			
            System.out.print(phone_number_id);
			messageService.sendMessage(phone_number_id, messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }





    private void sendListSponceredChildMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("Api will be call to get the Sponcered Childs");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }


     private void sendListDonationessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("API will be call to get the List Donation");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }




    private void sendListLatestNewsMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("API will be call to get the List Latest News Message");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }




    

}
