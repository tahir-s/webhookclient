package com.attribe.webhookclient.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.attribe.webhookclient.entity.SystemActivityLog;
import com.attribe.webhookclient.repository.SystemActivityLogRepository;


@Service
public class SystemActivityLogService {
	
	@Autowired
	private SystemActivityLogRepository systemActivityLogRepository;
	
	/**
	 * Save a new system activity log
	 * 
	 * @param systemActivityLog the activity log to save
	 * @return the saved activity log
	 */
	public SystemActivityLog saveActivityLog(SystemActivityLog systemActivityLog) {
		return systemActivityLogRepository.save(systemActivityLog);
	}
	
	/**
	 * Get activity log by ID
	 * 
	 * @param id the activity log ID
	 * @return Optional containing the activity log if found
	 */
	public Optional<SystemActivityLog> getActivityLogById(Long id) {
		return systemActivityLogRepository.findById(id);
	}
	
	/**
	 * Get activity log by message ID
	 * 
	 * @param messageId the message ID
	 * @return Optional containing the activity log if found
	 */
	public Optional<SystemActivityLog> getActivityLogByMessageId(String messageId) {
		return systemActivityLogRepository.findByMessageId(messageId);
	}
	
	/**
	 * Get all activity logs by message sender
	 * 
	 * @param messageFrom the message sender
	 * @return list of activity logs
	 */
	public List<SystemActivityLog> getActivityLogsByMessageFrom(String messageFrom) {
		return systemActivityLogRepository.findByMessageFrom(messageFrom);
	}
	
	/**
	 * Get all activity logs by contact WhatsApp ID
	 * 
	 * @param contactWaId the contact WhatsApp ID
	 * @return list of activity logs
	 */
	public List<SystemActivityLog> getActivityLogsByContactWaId(String contactWaId) {
		return systemActivityLogRepository.findByContactWaId(contactWaId);
	}
	
	/**
	 * Get activity logs within a time range
	 * 
	 * @param startTime the start time
	 * @param endTime the end time
	 * @return list of activity logs within the time range
	 */
	public List<SystemActivityLog> getActivityLogsByTimeRange(String startTime, String endTime) {
		return systemActivityLogRepository.findByMessageTimestampBetween(startTime, endTime);
	}
	
	/**
	 * Get activity logs by message type
	 * 
	 * @param messageType the message type
	 * @return list of activity logs
	 */
	public List<SystemActivityLog> getActivityLogsByMessageType(String messageType) {
		return systemActivityLogRepository.findByMessageType(messageType);
	}
	
	/**
	 * Get activity logs by interactive type
	 * 
	 * @param interactiveType the interactive type
	 * @return list of activity logs
	 */
	public List<SystemActivityLog> getActivityLogsByInteractiveType(String interactiveType) {
		return systemActivityLogRepository.findByInteractiveType(interactiveType);
	}
	
	/**
	 * Get all activity logs
	 * 
	 * @return list of all activity logs
	 */
	public List<SystemActivityLog> getAllActivityLogs() {
		return systemActivityLogRepository.findAll();
	}
	
	/**
	 * Update an activity log
	 * 
	 * @param systemActivityLog the activity log to update
	 * @return the updated activity log
	 */
	public SystemActivityLog updateActivityLog(SystemActivityLog systemActivityLog) {
		return systemActivityLogRepository.save(systemActivityLog);
	}
	
	/**
	 * Delete an activity log by ID
	 * 
	 * @param id the activity log ID
	 */
	public void deleteActivityLog(Long id) {
		systemActivityLogRepository.deleteById(id);
	}
	
	/**
	 * Check if activity log exists
	 * 
	 * @param id the activity log ID
	 * @return true if exists, false otherwise
	 */
	public boolean activityLogExists(Long id) {
		return systemActivityLogRepository.existsById(id);
	}

}
