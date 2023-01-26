package com.ant.svtelegrambot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Component
@EnableScheduling

class BotInit {
    @Autowired
    private var bot: Bot? = null

    @EventListener(ContextRefreshedEvent::class)
    fun initBot() {
        val teleBotApi: TelegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        teleBotApi.registerBot(bot)
    }
}