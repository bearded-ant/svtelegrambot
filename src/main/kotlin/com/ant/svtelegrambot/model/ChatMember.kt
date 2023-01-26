package com.ant.svtelegrambot.model

data class ChatMember(
    val id: String = "",
    val name: String = "",
    val chatId: String = "",
    val timeProperties: List<Int> = mutableListOf<Int>(1, 5, 10, 15, 25)

)
