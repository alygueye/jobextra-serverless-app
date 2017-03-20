package com.andado.jobextra.pojo;

import com.andado.jobextra.model.pets.Pet;

import java.util.List;

/**
 * Bean for the list pets response.
 */
public class ListPetsResponse {
  private int count;
  private int pageLimit;
  private List<Pet> pets;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getPageLimit() {
    return pageLimit;
  }

  public void setPageLimit(int pageLimit) {
    this.pageLimit = pageLimit;
  }

  public List<Pet> getPets() {
    return pets;
  }

  public void setPets(List<Pet> pets) {
    this.pets = pets;
  }
}
