package com.data;

/** Cases is a data container for the number of confirmed, deceased, recovered and tested cases. */
public class Cases {
  private long confirmed;
  private long deceased;
  private long recovered;
  private long tested;

  public Cases(long confirmed) {
    this.confirmed = confirmed;
  }

  public Cases(long confirmed, long deceased, long recovered, long tested) {
    this.confirmed = confirmed;
    this.deceased = deceased;
    this.recovered = recovered;
    this.tested = tested;
  }
}