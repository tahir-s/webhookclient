package com.attribe.webhookclient.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.attribe.webhookclient.entity.Client;
import com.attribe.webhookclient.pojo.client.ClientDTO;
import com.attribe.webhookclient.repository.ClientRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientSerivces {
	
	
	@Autowired
	private ClientRepository clientRepo;
	
	
	public ClientDTO getClientByClientId(String clientId) throws Exception{
		
		Client client = clientRepo.findByClientId(clientId).orElseThrow(() -> new EntityNotFoundException("Client IS not found"));
		
		//TODO get it form DB
		ClientDTO  clientDto= new ClientDTO();
		clientDto.setClientId(clientId);
		clientDto.setPhoneNumberId(client.getPhoneNumberId()); //Form test number provided by FB
		
			
		return clientDto;
		
	}
	
	

}
