package backend

import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create{config -> 
        config.addStaticFiles("/public")
    }.start(getHerokuAssignedPort())

    app.get("/api/") { ctx -> ctx.html("HEY! YOU WANNA SMELL SOMETHING <a href=\"https://www.youtube.com/watch?v=Ng769Yj-LG8\">SPACEY?!</a>") }
}

fun getHerokuAssignedPort(): Int {
    val port: String = System.getenv("PORT") ?: "7000"
    return port.toInt() 
}
