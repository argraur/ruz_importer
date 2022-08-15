package dev.argraur.ruzimporter

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Collections


class App(args: Array<String>) {
    companion object {
        const val APPLICATION_NAME = "ruz.spbstu.ru Importer"
        val JSON_FACTORY = GsonFactory.getDefaultInstance()
        private const val TOKENS_DIRECTORY_PATH = "tokens"
        private val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR)
        private const val CREDENTIALS_FILE_PATH = "/credentials.json"

        fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
            val inputStream = App::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH) ?: throw FileNotFoundException("You should put Google Calendar credentials file to src/main/resources/credentials.json")
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
            val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .build()
            val receiver = LocalServerReceiver.Builder().setPort(8888).build()
            val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
            return credential
        }
    }

    private val appConfig: AppConfig

    init {
        appConfig =
            if (args.isEmpty()) {
                AppConfig()
            } else {
                AppConfig(args)
            }
        Calendar(appConfig.facultyId, appConfig.groupId)
    }
}

fun main(args: Array<String>) {
    App(args)
}