package com.andado.jobextra.pojo;

import com.andado.jobextra.model.pets.Pet;

import java.util.List;

import lombok.Data;

/**
 * Bean for the list pets response.
 */
@Data
public class ListResponse<T>{
  private int count;
  private int pageLimit;
  private List<T> result;
}
