package com.attribe.webhookclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageTemplateService {

    @Value("${whatsapp.menu.heading}")
    private String menuHeading;

    @Value("${whatsapp.menu.footer}")
    private String menuFooter;

    @Value("${whatsapp.menu.button.track}")
    private String buttonTrack;

    @Value("${whatsapp.menu.button.shop}")
    private String buttonShop;

    @Value("${whatsapp.menu.button.support}")
    private String buttonSupport;

    @Value("${whatsapp.menu.button.chat-agent}")
    private String buttonChatAgent;

    /**
     * Builds an interactive menu message payload for WhatsApp
     * @param phoneNumber The recipient's phone number
     * @return Map containing the complete message payload
     */
    public Map<String, Object> buildMenuMessagePayload(String phoneNumber) {
        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("messaging_product", "whatsapp");
        messagePayload.put("to", phoneNumber);
        messagePayload.put("type", "interactive");

        Map<String, Object> interactive = new HashMap<>();
        interactive.put("type", "button");

        // Build body with heading
        Map<String, Object> body = new HashMap<>();
        body.put("text", menuHeading);
        interactive.put("body", body);

        // Build action with buttons
        Map<String, Object> action = new HashMap<>();
        action.put("buttons", buildMenuButtons());
        interactive.put("action", action);

        // Build footer
        Map<String, Object> footer = new HashMap<>();
        footer.put("text", menuFooter);
        interactive.put("footer", footer);

        messagePayload.put("interactive", interactive);
        return messagePayload;
    }

    /**
     * Creates the menu buttons
     * @return List of button objects
     */
    private List<Map<String, Object>> buildMenuButtons() {
        List<Map<String, Object>> buttons = new ArrayList<>();
        buttons.add(createButton("lst_sponcered_childs", buttonTrack));
        buttons.add(createButton("lst_last_donations", buttonShop));
       // buttons.add(createButton("lst_contact_detais", buttonSupport));
        buttons.add(createButton("chat_agent", buttonChatAgent)); // Max allowed buttons: 3"
        return buttons;
    }

    /**
     * Creates a single interactive button
     * @param id Unique button identifier
     * @param title Button display text
     * @return Map representing the button
     */
    private Map<String, Object> createButton(String id, String title) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("id", id);
        reply.put("title", title);

        Map<String, Object> button = new HashMap<>();
        button.put("type", "reply");
        button.put("reply", reply);

        return button;
    }
}
