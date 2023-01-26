package com.ant.svtelegrambot.parser

import com.ant.svtelegrambot.model.ResourceLocation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.stereotype.Component

@Component
class ParseLocation {

    fun createStatusObjects(): List<ResourceLocation> {
        val resourceLocationObjectList = mutableListOf<ResourceLocation>()
        val parserLocation = parseLocationFromUrl()

        for (i in 0 until parserLocation.lastIndex) {

            val nameLocation = getLocationName(parserLocation, i)
            val nexAttackTime = getNexAttackTime(parserLocation, i)
            val statusLocation = getStatusLocation(parserLocation, i)
            val timeToAttack = getTimeToAttack(statusLocation)

            resourceLocationObjectList.add(
                ResourceLocation(
                    name = nameLocation,
                    nextRun = nexAttackTime,
                    status = statusLocation.status,
                    underAttackFlag = statusLocation.attackFlag,
                    timeToAttack = timeToAttack
                )
            )
        }

        println(resourceLocationObjectList)
        return resourceLocationObjectList
    }

    private fun parseLocationFromUrl(): Elements {
        val url = "https://vsmuta.com/info/locs"
        val document: Document = Jsoup.connect(url).get()

        return document.select("div[class=m-auto]")
    }

    private fun getStatusLocation(parserLocation: Elements, i: Int): FlagAndString {
        val statusDanger = parserLocation[i]
            .select("div[class=mt-3]")
            .select("div[class=text-danger]")
            .text()

        val statusResourcesDanger = parserLocation[i]
            .select("div[class=text-danger mt-3]")
            .text()

        val statusSetOfFighters = parserLocation[i]
            .select("div[class=mt-3]")
            .select("div[class=text-warning fw-bold]")
            .text()

        val statusAttackIsOn = parserLocation[i]
            .select("div[class=mt-3]")
            .select("div[class=text-success fw-bold]")
            .text()

        val statusNormal = "До крика осталось: ${
            parserLocation[i]
                .select("div[class=mt-3]")
                .select("b")
                .text()
        }"

        return when (true) {
            statusDanger.isNotBlank() -> FlagAndString(true, statusDanger)
            statusSetOfFighters.isNotBlank() -> FlagAndString(false, statusSetOfFighters)
            statusAttackIsOn.isNotBlank() -> FlagAndString(false, statusAttackIsOn)
            statusResourcesDanger.isNotBlank() -> FlagAndString(true, statusResourcesDanger)
            else -> FlagAndString(false, statusNormal)
        }
    }

    private fun getTimeToAttack(statusString: FlagAndString) =
        if (statusString.status.length > 27 && statusString.attackFlag) statusString.status.substring(25, 27).trim()
            .toInt() else -1

    private fun getNexAttackTime(parserLocation: Elements, i: Int) = parserLocation[i]
        .selectFirst("div[class=mt-3]")?.text()

    private fun getLocationName(parserLocation: Elements, i: Int) = parserLocation[i]
        .select("div[class=mt-2]")
        .select("b")
        .text()
}