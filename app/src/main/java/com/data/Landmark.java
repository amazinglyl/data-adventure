package com.data;

/** A landmark, represented by its lat and lng coordinates. */
public class Landmark {
  private double lat;
  private double lng;
  private String title;
  private String description;

  public Landmark(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
    this.title = "";
    this.description = "";
  }

  public Landmark(double lat, double lng, String title, String description) {
    this.lat = lat;
    this.lng = lng;
    this.title = title;
    this.description = description;
  }
}