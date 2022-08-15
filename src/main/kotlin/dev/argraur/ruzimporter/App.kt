package dev.argraur.ruzimporter

import com.google.api.services.calendar.model.Event
import kotlin.random.Random
import kotlin.random.nextInt

private const val MIN_COLOR_CODE = 1
private const val MAX_COLOR_CODE = 11

class App(args: Array<String>) {
    private val appConfig: AppConfig

    init {
        appConfig =
            if (args.isEmpty()) {
                AppConfig()
            } else {
                AppConfig(args)
            }
        var colorSet = mutableSetOf<Int>()
        val colorMap = mutableMapOf<String, Int>()
        val vEvents = RuzParser(facultyId = appConfig.facultyId, groupId = appConfig.groupId, startDate = appConfig.startDate).getEvents()
        val events = mutableListOf<Event>()
        val calendar = Calendar
        vEvents.forEach {
            val event: Event
            if (appConfig.rainbowMode) {
                val colorId = colorMap.getOrPut(it.summary.value) {
                    var color: Int
                    do {
                        color = Random(System.currentTimeMillis()).nextInt(MIN_COLOR_CODE..MAX_COLOR_CODE)
                    } while (colorSet.contains(color))
                    colorSet.add(color)
                    color
                }.toString()
                if (colorSet.size == (1..11).count()) {
                    colorSet = mutableSetOf()
                }
                event = calendar.createEvent(
                    it.summary.value,
                    it.location.value,
                    it.startDate.date,
                    it.endDate.date,
                    colorId
                )
            } else {
                event = calendar.createEvent(
                    it.summary.value,
                    it.location.value,
                    it.startDate.date,
                    it.endDate.date
                )
            }
            if (!appConfig.ignoreSoftSkills || (appConfig.ignoreSoftSkills && !it.summary.value.contains("SoftSkills"))) {
                println("Adding event: ${it.summary.value} in ${it.location.value} at ${it.startDate.value} until ${it.endDate.value}")
                events.add(event)
            }
        }
        calendar.addEventsToCalendar(appConfig.calendarId, events)
    }
}

fun main(args: Array<String>) {
    App(args)
}