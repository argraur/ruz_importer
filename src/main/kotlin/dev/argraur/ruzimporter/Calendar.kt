package dev.argraur.ruzimporter

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar

class Calendar(facultyId: Int, groupId: Int) {
    companion object {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    }

    val service = Calendar.Builder(HTTP_TRANSPORT, App.JSON_FACTORY, App.getCredentials(HTTP_TRANSPORT))
        .setApplicationName(App.APPLICATION_NAME)
        .build()

    init {
        val now = DateTime(System.currentTimeMillis())
        val events = service.events().list("primary")
            .setTimeMax(now)
            .setMaxResults(10)
            .execute()
        val items = events.items
        items.forEach {
            println("== Event START == ")
            println("Summary: ${it.summary}")
            println("Kind: ${it.kind}")
            println("== Event END ==")
        }
    }
}