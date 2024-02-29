package com.github.haganbmj

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint

@ServerEndpoint("/card/{id}/ws")
@ApplicationScoped
class CardSocket(
    val cardSessions: CardSessions,
) {

    @OnOpen
    fun open(session: Session, @PathParam("id") id: String) {
        Log.info("open@$id: ${session.id}")
        cardSessions.open(session, id)
    }

    @OnClose
    fun close(session: Session, @PathParam("id") id: String) {
        Log.info("close@$id: ${session.id}")
        cardSessions.close(session, id)
    }

    @OnError
    fun error(session: Session, @PathParam("id") id: String, throwable: Throwable) {
        Log.error("error@$id: ${session.id}", throwable)
        cardSessions.close(session, id)
    }
}
