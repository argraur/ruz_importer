package dev.argraur.ruzimporter

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.component.VEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.Period
import kotlin.streams.toList

class RuzParser(private val facultyId: Int, private val groupId: Int, private val startDate: LocalDate = LocalDate.parse("2021-09-01")) {
    private val client = OkHttpClient()

    fun getEvents(): List<VEvent> {
        val events: MutableList<VEvent> = mutableListOf()
        val steps = startDate.datesUntil(LocalDate.now(), Period.ofDays(7)).toList()
        steps.forEach {
            val request = Request.Builder()
                .url("https://ruz.spbstu.ru/faculty/$facultyId/groups/$groupId/ical?date=$it")
                .build()
            val response = client.newCall(request).execute()
            val builder = CalendarBuilder()
            val calendar = builder.build(response.body!!.byteStream())
            val eventList = calendar.getComponents(Component.VEVENT)
            eventList.forEach {
                events.add(it as VEvent)
                println("Parsed event: ${it.summary.value} in ${it.location.value} at ${it.startDate.value} until ${it.endDate.value}")
            }
        }
        return events
    }
}