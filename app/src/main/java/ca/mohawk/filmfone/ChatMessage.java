package ca.mohawk.filmfone;

import java.time.LocalDate;

public class ChatMessage {
    int sentBy;
    String message;
    LocalDate date;

    /**
     * Chat message constructor
     * @param sentby 1 for messaged received (shows on left), 0 for messages sent (shows on right)
     * @param msg the message
     */
    public ChatMessage(int sentby, String msg) {
        this.sentBy = sentby;
        this.message = msg;
        this.date = LocalDate.now();
    }

    public int getSentBy() {return sentBy;}
    public String getMessage() {return message;}
    public LocalDate getDate() {return date;}
}
