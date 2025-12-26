package com.attribe.webhookclient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attribe.webhookclient.entity.Client;


public interface ClientRepository extends JpaRepository<Client, Long> {
	
	Optional<Client> findByClientId(String email);
	
	Optional<Client> findByPhoneNumberId(String phoneNumberId);

}
