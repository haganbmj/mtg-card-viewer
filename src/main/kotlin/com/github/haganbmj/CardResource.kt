package com.github.haganbmj

import com.github.haganbmj.models.CardUpdate
import io.quarkus.logging.Log
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.sse.OutboundSseEvent
import jakarta.ws.rs.sse.Sse
import jakarta.ws.rs.sse.SseBroadcaster
import jakarta.ws.rs.sse.SseEventSink
import org.jboss.resteasy.reactive.ResponseHeader
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// fetch('http://localhost:8081/card/abc', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({'id': 'abc', 'url': 'https://cards.scryfall.io/normal/front/0/6/06b2b8a4-3174-4188-959c-f456f9fcd563.jpg?1708638161'}) })
@Path("/card")
@ApplicationScoped
class CardResource(
    @Location("CardResource/card.html") val cardTemplate: Template
) {

    // TODO: Determine how to release these Broadcaster objects while accounting for multiple subscriptions.
    //  Could maybe just do some Atomic that counts the number of requests contexts tied to it?
    val broadcastMap = ConcurrentHashMap<String, SseBroadcaster>()
    val lastSentEventMap = ConcurrentHashMap<String, OutboundSseEvent>()

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun render(
        @PathParam("id") id: String,
    ) : TemplateInstance {
        Log.info("page: $id")
        return cardTemplate.data("id", id)
    }

    /**
     * TODO: Consider supporting by name assignment for other contexts.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun set(
        @PathParam("id") id: String,
        cardUpdate: CardUpdate,
        @Context sse: Sse,
    ) {
        // TODO: Validate the url being passed is not just random garbage.
        Log.info("set: id=$id, update=$cardUpdate")
        val event = sse.newEventBuilder()
            .id(UUID.randomUUID().toString())
            .name("card_update")
            .mediaType(MediaType.APPLICATION_JSON_TYPE)
            .data(String::class.java, cardUpdate.url)
            .build()

        broadcastMap[id]?.let {
            Log.info("broadcast: id=$id, event=$event")
            it.broadcast(event)
        }

        lastSentEventMap[id] = event
    }

    @GET
    @Path("/{id}/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @ResponseHeader(name = "Cache-Control", value = ["no-cache"])
    @ResponseHeader(name = "X-Accel-Buffering", value = ["no"])
    fun sse(
        @PathParam("id") id: String,
        @HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") lastEventId: Int,
        @Context sse: Sse,
        @Context sink: SseEventSink,
    ) {
        Log.info("sse: id=$id, lastEventId=$lastEventId")
        broadcastMap.computeIfAbsent(id) {
            sse.newBroadcaster()
        }.register(sink)

        // Send the last known event on an initial connection.
        lastSentEventMap[id]?.let { sink.send(it) }
    }
}
