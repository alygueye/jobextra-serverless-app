
package com.andado.jobextra.dao.pets;


import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.model.pets.Pet;

import java.util.List;

/**
 * This interface defines the methods required for an implementation of the PetDao object
 */
public interface PetDao {
  /**
   * Creates a new pet in the data store
   *
   * @param pet The pet object to be created
   * @return The generated petId
   * @throws DAOException Whenever an error occurs while accessing the data store
   */
  String createPet(Pet pet) throws DAOException;

  /**
   * Retrieves a Pet object by its id
   *
   * @param petId The petId to look for
   * @return An initialized and populated Pet object. If the pet couldn't be found return null
   * @throws DAOException Whenever a data store access error occurs
   */
  Pet getPetById(String petId) throws DAOException;

  List<Pet> getPets(int limit);
}
