package dev.argraur.ruzimporter

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

import kotlin.system.exitProcess

class AppConfig {
    val groupId: Int
    val facultyId: Int

    fun validateGroupId(groupId: String): Boolean {
        if (groupId.toIntOrNull() != null) {
            return groupId.length == 5
        } else {
            return false
        }
    }

    fun validateFacultyId(facultyId: String): Boolean {
        if (facultyId.toIntOrNull() != null) {
            return facultyId.length in 2..3
        } else {
            return false
        }
    }

    constructor() {
        print("Enter your faculty ID: ")
        val facultyIdPending = readln()
        if (validateFacultyId(facultyIdPending)) {
            facultyId = facultyIdPending.toInt()
        } else {
            println("Error! Faculty ID is 2 digit number you can find in address bar when opening your calendar. (ruz.spbstu.ru/faculty/[facultyId]/groups/[groupId])")
            exitProcess(1)
        }
        print("Enter your group ID: ")
        val groupIdPending = readln()
        if (validateGroupId(groupIdPending)) {
            groupId = groupIdPending.toInt()
        } else {
            println("Error! Group ID is 5 digit number you can find in address bar when opening your calendar. (ruz.spbstu.ru/faculty/[facultyId]/groups/[groupId])")
            exitProcess(1)
        }
    }

    constructor(args: Array<String>) {
        val parser = ArgParser("ruz_importer")
        val facultyIdParse by parser.option(ArgType.Int, fullName = "facultyId", shortName = "f", description = "Faculty ID").default(95)
        val groupIdParse by parser.option(ArgType.Int, fullName = "groupId", shortName = "g", description = "Group ID").default(33693)
        parser.parse(args)
        facultyId = facultyIdParse
        groupId = groupIdParse
    }
}