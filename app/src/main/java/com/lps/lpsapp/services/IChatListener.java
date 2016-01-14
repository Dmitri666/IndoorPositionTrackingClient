package com.lps.lpsapp.services;

import com.lps.lpsapp.viewModel.chat.Actor;
import com.lps.lpsapp.viewModel.chat.ChatMessage;

/**
 * Created by dle on 20.08.2015.
 */
public interface IChatListener {
    void messageResived(ChatMessage chatMessage);
    void joinChat(Actor actor);
    void leaveChat(Actor actor);
}
