package com.doftom.models;

import java.util.Date;

public class SearchEvent {
  private String id;
  private String searchData;

  public SearchEvent(String searchData) {
    this.setSearchData(searchData);
  }

  public SearchEvent() {
    this.setSearchData(null);
  }

  public void setSearchData(String searchData) {
    if (searchData != null) {
      this.id = String.valueOf(new Date().getTime());
      this.searchData = searchData;
    }
  }

  public String getSearchData() {
    return searchData;
  }

  public String getId() {
    return id;
  }
}
