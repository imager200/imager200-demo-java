package io.imager200;

import java.io.InputStream;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ImagePayload {

  @FormParam("image")
  @PartType(MediaType.APPLICATION_OCTET_STREAM)
  public InputStream file;


  @FormParam("operation")
  @PartType(MediaType.TEXT_PLAIN)
  public Operation operation;

  public enum Operation {
    grayscale, equalize, blur
  }
}
