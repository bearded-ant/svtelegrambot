package com.ant.svtelegrambot

import com.ant.svtelegrambot.firebase.ChatMembersCallback
import com.ant.svtelegrambot.firebase.FirebaseRepo
import com.ant.svtelegrambot.firebase.LocationsCallback
import com.ant.svtelegrambot.model.ChatMember
import com.ant.svtelegrambot.model.ResourceLocation
import com.ant.svtelegrambot.parser.ParseLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


@Component
class Bot : TelegramLongPollingBot() {
    @Autowired
    private val firebase: FirebaseRepo? = null

    @Value("\${bot.name}")
    private val botUsername: String? = null

    @Value("\${bot.token}")
    private val botToken: String? = null

    private val userTimeOptionsFlag = mutableSetOf<String>()

    override fun onUpdateReceived(update: Update) {
        when (update.message.text) {
            "/start" -> {
                val userName = update.message.from.userName ?: "anonymous"
                val userId = update.message.from.id.toString()
                val userChatId = update.message.chatId.toString()
                val user = ChatMember(userId, userName, userChatId)
                val dbUserFormat = mutableMapOf<String, ChatMember>()
                dbUserFormat[user.id] = user
                firebase!!.dbUserRef.updateChildrenAsync(dbUserFormat as Map<String, Any>)
                sendDefaultMessage(userChatId, "привет, $userName")
            }

            "/show" -> {
                firebase!!.getChatMember(object : ChatMembersCallback {
                    override fun onChatMemberCallback(chatMembers: List<ChatMember>) {
                        for (chatMember in chatMembers)
                            if (chatMember.chatId == update.message.chatId.toString())
                                sendDefaultMessage(chatMember.chatId, chatMember.timeProperties.toString())
                    }
                })
            }

            "/set" -> {
                sendDefaultMessage(
                    update.message.chatId.toString(),
                    "введите интервалы оповещения через запятую от 0 до 25"
                )
                userTimeOptionsFlag.add(update.message.chatId.toString())
            }

            "/status" -> {
                firebase!!.getLocation(object : LocationsCallback {
                    override fun onLocationCallback(data: List<ResourceLocation>) {
                        for (post in data)
                            sendDefaultMessage(update.message.chatId.toString(), setTextDecoration(post))
                    }

                })
            }

            "/underattack" -> {
                firebase!!.getLocation(object : LocationsCallback {
                    override fun onLocationCallback(data: List<ResourceLocation>) {
                        var attacked = false
                        for (post in data) {
                            if (post.underAttackFlag || post.status == "Локация занята монстрами") {
                                sendDefaultMessage(update.message.chatId.toString(), setTextDecoration(post))
                                attacked = true
                            }
                        }
                        if (!attacked)
                            sendDefaultMessage(update.message.chatId.toString(), "странно, но голову откусить некому")
                    }
                })
            }

            else ->
                try {
                    val chatId = update.message.chatId.toString()

                    if (chatId in userTimeOptionsFlag) {
                        val timeOption = mutableSetOf<Int>()
                        userTimeOptionsFlag.remove(chatId)
                        val str = (update.message.text.toString()).split(",")
                        for (char in str) {
                            val intChar = char.trim().toIntOrNull()
                            if (intChar != null && (intChar in 0..25))
                                timeOption.add(intChar)
                        }
                        if (str.size == timeOption.size) {
                            sendDefaultMessage(chatId, "норм, $timeOption")
                            val childUpdateRef = firebase!!.dbUserRef.child(chatId)
                            val hopperUpdates = mutableMapOf<String, Any>()
                            hopperUpdates.put("timeProperties", timeOption.toList())

                            childUpdateRef.updateChildrenAsync(hopperUpdates)
                        } else
                            sendDefaultMessage(
                                chatId,
                                "Строка должна содержать только цифры в интервале от 0 до 25, без повторов. Попробуйте снова."
                            )
                    } else
                        sendDefaultMessage(chatId, "${update.message.from.userName}, чего тебе?")
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
        }
    }

    override fun getBotUsername(): String = botUsername!!

    override fun getBotToken(): String = botToken!!

    @Scheduled(cron = "0 0/1 * * * ?")
    fun autoMessage() {
        val locations = updateDataBase()
        val chatMembers = getChatMembers()

        for (i in 0..chatMembers.lastIndex)
            for (j in 0..locations.lastIndex)
                if (locations[j].underAttackFlag && (locations[j].timeToAttack in chatMembers[i].timeProperties)) {
                    val messageString = setTextDecoration(locations[j])
                    val buttons = setInlineStatusButton(locations[j].status)
                    sendUnderAttackMessage(chatMembers[i].chatId, buttons, messageString)
                }
    }

    private fun sendDefaultMessage(chatId: String, messageText: String) {
        val send = SendMessage()
        send.chatId = chatId
        send.parseMode = "html"
        send.text = messageText
        execute(send)
    }

    fun sendUnderAttackMessage(chatId: String, buttons: InlineKeyboardMarkup, messageText: String) {
        val send = SendMessage()
        send.chatId = chatId
        send.text = messageText
        send.parseMode = "html"
        send.replyMarkup = buttons
        execute(send)
    }

    fun updateDataBase(): List<ResourceLocation> {
        val locations = ParseLocation().createStatusObjects()
        val dataHashMap = mutableMapOf<String, ResourceLocation>()

        for (i in 0..locations.lastIndex)
            dataHashMap[locations[i].name] = locations[i]

        firebase!!.dbLocationsRef.updateChildrenAsync(dataHashMap as Map<String, Any>?)
        return locations
    }

    private fun getChatMembers(): MutableList<ChatMember> {
        val users = mutableListOf<ChatMember>()
        firebase!!.getChatMember(object : ChatMembersCallback {
            override fun onChatMemberCallback(chatMembers: List<ChatMember>) {
                users.addAll(chatMembers)
            }
        })
        return users
    }

    fun setTextDecoration(resourceLocation: ResourceLocation): String {
        val decoratedMessage = StringBuilder()
        decoratedMessage.append("<b>${resourceLocation.name}</b>")
        if (resourceLocation.status != "До крика осталось: ")
            decoratedMessage.append("<pre>\n${resourceLocation.status}</pre>")
        decoratedMessage.append("<pre>\n${resourceLocation.nextRun}</pre>")
        return decoratedMessage.toString()
    }

    fun setInlineStatusButton(buttonText: String): InlineKeyboardMarkup {
        val markupInline = InlineKeyboardMarkup()
        val rowInline = mutableListOf<InlineKeyboardButton>()
        val inlineKeyboardButton = InlineKeyboardButton()

        inlineKeyboardButton.text = buttonText
        inlineKeyboardButton.callbackData = "call"

        rowInline.add(inlineKeyboardButton)
        markupInline.keyboard = listOf(rowInline)
        return markupInline
    }
}