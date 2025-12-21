package com.attribe.webhookclient.service;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public class MessageType {
        public static final String test = "text";
        public static final String interactive = "interactive";

    }

    public class UserChatChache {
        public static Map<String, String> cache =  new HashMap<>();
        
    }

}
