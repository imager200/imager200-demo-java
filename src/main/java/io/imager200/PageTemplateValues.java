package io.imager200;

import java.util.List;

public class PageTemplateValues {

  List<String> imageUrls;

  boolean error;

  String errorMessage;

  public PageTemplateValues(List<String> imageUrls, boolean error, String errorMessage) {
    this.imageUrls = imageUrls;
    this.error = error;
    this.errorMessage = errorMessage;
  }

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public boolean isError() {
    return error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
