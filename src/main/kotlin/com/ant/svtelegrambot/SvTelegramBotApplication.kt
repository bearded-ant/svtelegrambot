package com.ant.svtelegrambot

import com.ant.svtelegrambot.firebase.FireBaseInit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
class SvTelegramBotApplication

fun main(args: Array<String>) {

    FireBaseInit().firebaseInit()

    runApplication<SvTelegramBotApplication>(*args)
}
