package io.imager200;

import io.quarkus.qute.Template;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;

@Provider
public class ClientExceptionMapper implements ExceptionMapper<ResteasyWebApplicationException> {

  @Inject
  Template page;


  @Override
  public Response toResponse(ResteasyWebApplicationException exception) {
    String clientResponse = exception.unwrap().getResponse().readEntity(String.class);
    return Response.status(Status.OK).entity(page.data("values", new PageTemplateValues(List.of(), true, exception.getMessage() + ": " + clientResponse)).render()).type(
        MediaType.TEXT_HTML_TYPE).build();
  }
}
