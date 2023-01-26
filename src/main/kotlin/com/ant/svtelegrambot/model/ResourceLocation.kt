package com.ant.svtelegrambot.model

data class ResourceLocation(
    val name: String ="",
    val nextRun: String? = "локация не захвачена",
    val status: String = "не известно",
    val underAttackFlag: Boolean = false,
    var messageAlreadySendFlag: Boolean  = false,
    val timeToAttack: Int = -1
)