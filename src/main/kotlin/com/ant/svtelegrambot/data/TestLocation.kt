package com.ant.svtelegrambot.data

import com.ant.svtelegrambot.model.ResourceLocation

class TestLocation {
    val data = mutableListOf<ResourceLocation>()

    fun dataInit():List<ResourceLocation> {
        data.add(
            ResourceLocation(
                messageAlreadySendFlag = false,
                name = "Ближний Лес",
                status = "До крика осталось: ",
                timeToAttack = 18,
                underAttackFlag = true
            )
        )
        data.add(
            ResourceLocation(
                messageAlreadySendFlag = false,
                name = "Дальний буй",
                status = "проверка",
                timeToAttack = 1,
                underAttackFlag = true
            )
        )
        return data
    }
}