package com.ant.svtelegrambot.firebase

import com.ant.svtelegrambot.model.ChatMember

interface ChatMembersCallback {
    fun onChatMemberCallback(chatMembers: List<ChatMember>)
}