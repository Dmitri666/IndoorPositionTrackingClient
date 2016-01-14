package com.lps.lpsapp.viewModel.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dle on 22.10.2015.
 */
public class ConversationsData {
    public ConversationsData()
    {
        this.messages = new ArrayList<>();
    }
    public UUID conversationId;
    public UUID userId;
    public String userName;

    public List<ChatMessage> messages;
}
