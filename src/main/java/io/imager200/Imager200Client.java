package io.imager200;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@RegisterClientHeaders
public interface Imager200Client {

  @POST
  @Path("/grayscale")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @Produces(MediaType.APPLICATION_JSON)
  Response grayscale(InputStream image, @HeaderParam("X-Imager-Key") String apiKey);


  @POST
  @Path("/equalize")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @Produces(MediaType.APPLICATION_JSON)
  Response equalize(InputStream image, @HeaderParam("X-Imager-Key") String apiKey);

  @POST
  @Path("/blur")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @Produces(MediaType.APPLICATION_JSON)
  Response blur(InputStream image, @HeaderParam("X-Imager-Key") String apiKey);
}