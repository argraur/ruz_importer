package dev.argraur.ruzimporter

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.batch.BatchCallback
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpHeaders
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.util.*

object Calendar {
    private val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private const val APPLICATION_NAME = "ruz.spbstu.ru Importer"
    private val JSON_FACTORY: GsonFactory = GsonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"
    private val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR)
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"

    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val inputStream = App::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("You should put Google Calendar credentials file to src/main/resources/credentials.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    private val service: Calendar = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build()

    fun createEvent(summary: String, location: String, startDate: Date, endDate: Date, colorId: String? = null): Event {
        return if (colorId == null) {
            Event()
                .setSummary(summary)
                .setLocation(location)
                .setStart(EventDateTime().setDateTime(DateTime(startDate)))
                .setEnd(EventDateTime().setDateTime(DateTime(endDate)))
        } else {
            Event()
                .setSummary(summary)
                .setLocation(location)
                .setStart(EventDateTime().setDateTime(DateTime(startDate)))
                .setEnd(EventDateTime().setDateTime(DateTime(endDate)))
                .setColorId(colorId)
        }
    }

    fun addEventsToCalendar(calendarId: String, events: List<Event>) {
        val batch = service.batch()
        events.forEach {
            batch.queue(service.events().insert(calendarId, it).buildHttpRequest(), Event::class.java, Void::class.java, object : BatchCallback<Event, Void> {
                override fun onSuccess(t: Event?, responseHeaders: HttpHeaders?) {
                    println("Successfully added event: ${it.summary} in ${it.location} at ${it.start.dateTime.toStringRfc3339()} until ${it.end.dateTime.toStringRfc3339()}")
                }

                override fun onFailure(e: Void?, responseHeaders: HttpHeaders?) {
                    println("Failed to add event!")
                    throw IllegalStateException()
                }
            })
            println("Added event ${it.summary} in ${it.location} at ${it.start.dateTime.toStringRfc3339()} until ${it.end.dateTime.toStringRfc3339()} to the batch.")
        }
        println("Executing batch request...")
        batch.execute()
    }

    fun createStandardCalendar(facultyId: Int, groupId: Int): String {
        return service.calendars().insert(com.google.api.services.calendar.model.Calendar()
                .setSummary("Расписание занятий $facultyId/$groupId")
        ).execute().id
    }
}