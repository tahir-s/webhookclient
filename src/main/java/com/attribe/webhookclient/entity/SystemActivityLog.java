package com.attribe.webhookclient.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class SystemActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //Message
    private String messageId;
    private String messageFrom;
    private String messageTimestamp;
    private String messageText;
    private String messageType;

    //Interactive
    private String interactiveType;
    private String interactiveButtonId;
    private String interactiveButtonTitle;

    //Contact
    private String contactWaId;
    private String contactProfileName;




    //Context
    private String contextFrom;
    private String contextId;


    //Metadata
    private String metadataDisplayPhonNumber;
    private String metadataPhoneNumberId;
    

}

