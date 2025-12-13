package com.attribe.webhookclient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.attribe.webhookclient.entity.SystemActivityLog;


@Repository
public interface SystemActivityLogRepository extends JpaRepository<SystemActivityLog, Long> {
	
	Optional<SystemActivityLog> findByMessageId(String messageId);
	
	List<SystemActivityLog> findByMessageFrom(String messageFrom);
	
	List<SystemActivityLog> findByContactWaId(String contactWaId);
	
	@Query("SELECT sal FROM SystemActivityLog sal WHERE sal.messageTimestamp BETWEEN :startTime AND :endTime")
	List<SystemActivityLog> findByMessageTimestampBetween(@Param("startTime") String startTime, @Param("endTime") String endTime);
	
	List<SystemActivityLog> findByMessageType(String messageType);
	
	List<SystemActivityLog> findByInteractiveType(String interactiveType);

}
