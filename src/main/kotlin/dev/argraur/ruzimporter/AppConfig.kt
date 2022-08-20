package dev.argraur.ruzimporter

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.time.LocalDate
import java.util.Date

import kotlin.system.exitProcess

class AppConfig {
    val groupId: Int
    val facultyId: Int
    val calendarId: String
    val startDate: LocalDate
    var endDate: LocalDate = LocalDate.now()
    val ignoreSoftSkills: Boolean
    val rainbowMode: Boolean

    private fun validateGroupId(groupId: String): Boolean {
        return if (groupId.toIntOrNull() != null) {
            groupId.length == 5
        } else {
            false
        }
    }

    private fun validateFacultyId(facultyId: String): Boolean {
        if (facultyId.toIntOrNull() != null) {
            return facultyId.length in 2..3
        } else {
            return false
        }
    }

    constructor() {
        print("Enter your faculty ID: ")
        val facultyIdPending = readLine()
        if (validateFacultyId(facultyIdPending!!)) {
            facultyId = facultyIdPending.toInt()
        } else {
            println("Error! Faculty ID is 2 digit number you can find in address bar when opening your calendar. (ruz.spbstu.ru/faculty/[facultyId]/groups/[groupId])")
            exitProcess(1)
        }
        print("Enter your group ID: ")
        val groupIdPending = readLine()
        if (validateGroupId(groupIdPending!!)) {
            groupId = groupIdPending.toInt()
        } else {
            println("Error! Group ID is 5 digit number you can find in address bar when opening your calendar. (ruz.spbstu.ru/faculty/[facultyId]/groups/[groupId])")
            exitProcess(1)
        }
        print("Enter start date (YYYY-MM-DD): ")
        startDate = LocalDate.parse(readLine())
        print("Enter end date (YYYY-MM-DD) [Default: LocalDate.now()]: ")
        val endDateLine = readLine()
        if (endDateLine!!.isNotEmpty()) {
            endDate = LocalDate.parse(endDateLine)
        }
        print("Do you have an existing calendar where you want to import? (Y/n) ")
        var query = readLine()
        if (query!!.isEmpty() || query.lowercase() == "y") {
            println("Grab your calendar ID from Google Calendar web interface")
            println("To do so, open settings of your calendar of choice and scroll down to Calendar identificator (it looks like an E-mail)")
            print("Copy and paste it here: ")
            calendarId = readLine()!!
        } else {
            println("Creating new calendar for you...")
            calendarId = Calendar.createStandardCalendar(facultyId, groupId)
            println("Calendar ID: $calendarId")
        }
        print("Do you want Soft Skills to be shown (Y/n)? ")
        query = readLine()
        if (query!!.lowercase() == "n") {
            println("Ignoring Soft Skills!")
            ignoreSoftSkills = true
        } else {
            ignoreSoftSkills = false
        }
        print("Do you want your schedule to be colorful? (y/N) ")
        query = readLine()
        if (query!!.lowercase() == "y") {
            println("Enabled rainbow mode!")
            rainbowMode = true
        } else {
            rainbowMode = false
        }
    }

    constructor(args: Array<String>) {
        val parser = ArgParser("ruz_importer")
        val facultyIdParse by parser.option(ArgType.Int, fullName = "facultyId", shortName = "f", description = "Faculty ID").default(95)
        val groupIdParse by parser.option(ArgType.Int, fullName = "groupId", shortName = "g", description = "Group ID").default(33693)
        val calendarIdParse by parser.option(ArgType.String, fullName = "calendarId", shortName = "c", description = "Calendar ID").default("primary")
        val startDateParse by parser.option(ArgType.String, fullName = "startDate", shortName = "d", description = "Start date in format YYYY-MM-DD").default("2021-09-01")
        val ignoreSoftSkillsParse by parser.option(ArgType.Boolean, fullName="ignoreSofts", shortName = "is", description = "Ignore Soft Skills completely").default(false)
        val rainbowModeParse by parser.option(ArgType.Boolean, fullName="rainbow", shortName = "r", description = "Generate colorful schedule").default(false)
        parser.parse(args)
        facultyId = facultyIdParse
        groupId = groupIdParse
        calendarId = calendarIdParse
        startDate = LocalDate.parse(startDateParse)
        ignoreSoftSkills = ignoreSoftSkillsParse
        rainbowMode = rainbowModeParse
    }
}