package com.attribe.webhookclient.service.handle;


import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;


public interface ClientHandle {

    void handleInbondMessage(Metadata metadata, Message message);

}
