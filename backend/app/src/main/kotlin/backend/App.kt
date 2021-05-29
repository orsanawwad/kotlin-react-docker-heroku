package backend

import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create{config -> 
        config.addStaticFiles("/public")
    }.start(getHerokuAssignedPort())

    app.get("/api/") { ctx -> ctx.result("Hello Heroku") }
}

fun getHerokuAssignedPort(): Int {
    val port: String = System.getenv("PORT") ?: "7000"
    return port.toInt() 
}
