package com.github.haganbmj.resources

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
@ApplicationScoped
class RootResource(
    @Location("RootResource/index.html") val indexTemplate: Template,
) {

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun render() : TemplateInstance {
        return indexTemplate.instance()
    }
}
