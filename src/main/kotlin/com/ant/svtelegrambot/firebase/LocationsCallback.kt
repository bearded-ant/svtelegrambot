package com.ant.svtelegrambot.firebase

import com.ant.svtelegrambot.model.ResourceLocation

interface LocationsCallback {
    fun onLocationCallback(data: List<ResourceLocation>)
}