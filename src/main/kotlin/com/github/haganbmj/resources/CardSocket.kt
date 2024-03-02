package com.github.haganbmj.resources

import com.github.haganbmj.services.WebSocketSessionService
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
    val webSocketSessionService: WebSocketSessionService,
) {

    @OnOpen
    fun open(session: Session, @PathParam("id") id: String) {
        Log.info("open@$id: ${session.id}")
        webSocketSessionService.open(session, id)
    }

    @OnClose
    fun close(session: Session, @PathParam("id") id: String) {
        Log.info("close@$id: ${session.id}")
        webSocketSessionService.close(session, id)
    }

    @OnError
    fun error(session: Session, @PathParam("id") id: String, throwable: Throwable) {
        Log.error("error@$id: ${session.id}", throwable)
        webSocketSessionService.close(session, id)
    }
}
