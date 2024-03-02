package com.github.haganbmj

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.websocket.Session
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class CardSessions {

    val sessionMap = ConcurrentHashMap<String, ConcurrentHashMap<String, Session>>()
    val lastSentMessage = ConcurrentHashMap<String, String>()

    fun open(session: Session, id: String) {
        sessionMap.computeIfAbsent(id) {
            ConcurrentHashMap<String, Session>()
        }[session.id] = session
        lastSentMessage[id]?.let { send(session, it) }
    }

    fun close(session: Session, id: String) {
        sessionMap[id]?.remove(session.id)
    }

    fun broadcast(id: String, data: String) {
        Log.info("broadcast: id=$id, data=$data")
        sessionMap[id]?.values?.forEach { send(it, data) }
        lastSentMessage[id] = data
    }

    private fun send(session: Session, data: String) {
        session.asyncRemote.sendObject(data)
    }
}
