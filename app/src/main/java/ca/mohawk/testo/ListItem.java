package ca.mohawk.testo;

import java.util.ArrayList;

public class ListItem {
    String sender;
    ArrayList<ChatMessage> messages;
    public ListItem(String name, String msg) {
        this.sender = name;
        this.messages = new ArrayList<>();
        messages.add(new ChatMessage(1,msg));
    }
    public String getSender() {return sender;}
    public ArrayList<ChatMessage> getMessages() {return messages;}
    public void addMessage(int sb, String msg) {
        messages.add(new ChatMessage(sb, msg));
    }
}
