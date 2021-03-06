package io.imager200;

import static java.util.Objects.requireNonNull;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;

@Path("/")
public class MainPage {

    private static final Logger log = Logger.getLogger(MainPage.class);

    private final Template page;

    @Inject
    @RestClient
    Imager200Client service;

    @Inject
    ImageList imageList;

    //used to poll images until they are ready
    private final HttpClient client;

    @ConfigProperty(name = "imager200.api-key")
    String apiKey;


    public MainPage(Template page) {
        this.page = requireNonNull(page, "page is required");
        client = HttpClient.newBuilder().build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return page.data("values", new PageTemplateValues(imageList.imageUrls, false, ""));
    }


    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance process(@MultipartForm ImagePayload payload)
        throws Exception {
        log.infof("applying %s to image", payload.operation.toString());
        Response response;
        switch (payload.operation){
            case blur:
                response = service.blur(payload.file, apiKey);
                break;
            case equalize:
                response = service.equalize(payload.file, apiKey);
                break;
            default:
                response = service.grayscale(payload.file, apiKey);
        }
        if (response.getStatus() != 201) {
            throw new ResteasyWebApplicationException(new WebApplicationException(response));
        }
        String imageUrl = response.getHeaderString("Location");
        log.infof("request returned, waiting for image %s to be ready", imageUrl);

         //because of async requests, we don't know if the image is yet available
        // we wait to make sure we return a properly available image
        //https://guides.imager200.io/apiusage/aync-vs-sync/
        if (!waitForImage(imageUrl)) {
          return page.data("values", new PageTemplateValues(imageList.imageUrls, true, "image processing failed"));
        }

        imageList.imageUrls.add(imageUrl);
        return page.data("values", new PageTemplateValues(imageList.imageUrls, false, ""));
    }

    private boolean waitForImage(String imageUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(imageUrl)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse imageResponse = this.client.send(request, BodyHandlers.discarding());
            var i = 0;
            while (imageResponse.statusCode() != 200 && i < 10) {
                Thread.sleep(1000);
                imageResponse = this.client.send(request, BodyHandlers.discarding());
                i++;
            }

            if (imageResponse.statusCode() == 200) {
              return true;
            }

            return false;
    }
}