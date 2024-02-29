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
import jakarta.ws.rs.POST
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
    @Location("CardResource/card.html") val cardTemplate: Template,
    val cardSessions: CardSessions,
) {

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
     * TODO: Validate the url being passed is not just random garbage.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun set(
        @PathParam("id") id: String,
        cardUpdate: CardUpdate,
        @Context sse: Sse,
    ) {
        Log.info("set: id=$id, update=$cardUpdate")
        cardSessions.broadcast(id, cardUpdate.url)
    }
}
