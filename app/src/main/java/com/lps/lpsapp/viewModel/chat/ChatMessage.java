package com.lps.lpsapp.viewModel.chat;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Technovibe on 17-04-2015.
 */
public class ChatMessage {
    public UUID conversationMessageId;
    public UUID conversationId;
    public String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy'-'MM'-'dd'T'HH':'mm", timezone = "GMT")
    public Date time;
    public boolean isMe;

    public ChatMessage() {

    }


}
