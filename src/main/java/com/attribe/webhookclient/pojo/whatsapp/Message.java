package com.attribe.webhookclient.pojo.whatsapp;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class Message {
	private String from;
    private String id;
    private String timestamp;
    private Text text;
    private String type;

    private Interactive interactive; // for type=interactive
    private Context context; // optional, for replies
    
    
    @Override
    public String toString() {
    	System.out.println("Message -->");
    	System.out.println(" - from: " + from);
    	System.out.println(" - id: " + id);
    	System.out.println(" - text: " + text);
    	System.out.println(" - type: " + type);

        if(interactive!=null)
            interactive.toString();

        if(context!=null)
            context.toString();

    	return super.toString();
    }

}
