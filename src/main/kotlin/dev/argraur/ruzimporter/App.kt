package dev.argraur.ruzimporter

import com.google.api.services.calendar.model.Event

class App(args: Array<String>) {
    private val appConfig: AppConfig

    init {
        appConfig =
            if (args.isEmpty()) {
                AppConfig()
            } else {
                AppConfig(args)
            }
        val vEvents = RuzParser(facultyId = appConfig.facultyId, groupId = appConfig.groupId, startDate = appConfig.startDate).getEvents()
        val events = mutableListOf<Event>()
        val calendar = Calendar
        vEvents.forEach {
            val event = calendar.createEvent(
                it.summary.value,
                it.location.value,
                it.startDate.date,
                it.endDate.date
            )
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