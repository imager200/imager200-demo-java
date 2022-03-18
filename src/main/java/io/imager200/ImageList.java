package io.imager200;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImageList {

  public final List<String> imageUrls;


  public ImageList() {
    this.imageUrls = new ArrayList<>();
  }
}
