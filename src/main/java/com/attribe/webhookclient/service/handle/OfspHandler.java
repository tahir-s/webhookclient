package com.attribe.webhookclient.service.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.attribe.webhookclient.pojo.client.MessageDTO;
import com.attribe.webhookclient.pojo.whatsapp.Interactive;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;
import com.attribe.webhookclient.service.Constant;
import com.attribe.webhookclient.service.WhatsAppSendMenuService;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;

@Component("763421936848515")
public class OfspHandler implements  ClientHandle{



	@Autowired
	private WhatsAppSendMessageService messageService;

    @Autowired
    private WhatsAppSendMenuService menuService;

    


    @Override
    public void handleInbondMessage(Metadata metadata, Message message) {


       
		try {
            String commandRecived ="";
            String type =  message.getType()+"";
            String button_id = "";
            
            if(Constant.MessageType.test.equals(type)){
                commandRecived =  message.getText() != null ? message.getText().getBody() : "";
            }
            else if(Constant.MessageType.interactive.equals(type)){
                commandRecived= getButtonId(metadata, message);
            }

           System.out.println("Command Recived:---------- "+ commandRecived);

            switch (commandRecived.toLowerCase()) {
            case "lst_sponcered_childs": // List Sponcered Child
                sendListSponceredChildMessage(metadata, message);
                break;
            case "lst_last_donations": // List Donation
                sendListDonationessage(metadata, message);
                break;
            case "lst_contact_detais": // Get Latest News
                sendListLatestNewsMessage(metadata, message);
                break;
            case "chat_agent": // Chat Agent
                sendChatAgentMessage(metadata, message);
                break;
            case "m": // Send Menu
                sendMenuMessage(metadata, message);
                break;
            default:
                System.out.println("Unknown type!");
                sendMenuMessage(metadata, message);
        }

           
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
			
			messageDto.setBody("API will be call to get the List Donation. Mujtabab & TUba");
			
	
           
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


    private void sendChatAgentMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("Coming soon...");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }


    private void sendMenuMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("");
			
	
           
			menuService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }



    private String getButtonId(Metadata metadata, Message message) {
         
        String  button_id = "m"; // be default show menu

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
            
			
             Interactive interactive = message.getInteractive();
                if(interactive!=null){
                   button_id =  interactive.getButton_reply().getId();

                }
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

        return button_id;

    }

    

}
