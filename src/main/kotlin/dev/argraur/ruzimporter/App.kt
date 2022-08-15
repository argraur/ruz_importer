package dev.argraur.ruzimporter

class App(args: Array<String>) {
    private val appConfig: AppConfig

    init {
        appConfig =
            if (args.isEmpty()) {
                AppConfig()
            } else {
                AppConfig(args)
            }
    }
}

fun main(args: Array<String>) = App(args)