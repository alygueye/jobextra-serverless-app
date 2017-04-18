
package com.andado.jobextra.dao.pets;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.andado.jobextra.configuration.DynamoDBConfiguration;
import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.model.pets.Pet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The DynamoDB implementation of the PetDao object. This class expects the Pet bean to be annotated
 * with the required DynamoDB Object Mapper annotations. Configuration values for the class such as
 * table name and pagination rows is read from the DynamoDBConfiguration class . Credentials for the
 * DynamoDB client are read from the environment variables in the Lambda instance.
 * <p/>
 */
@Component
public class PetDaoImpl implements PetDao {

  // Initialize the Log4j logger.
  static final Logger LOG = Logger.getLogger(PetDaoImpl.class);

  @Autowired
  private DynamoDBMapper amazonDynamoDBMapper;

  /**
   * Creates a new Pet
   *
   * @param pet The pet object to be created
   * @return The id for the newly created Pet object
   */
  public String createPet(Pet pet) throws DAOException {
    if (pet.getPetType() == null || pet.getPetType().trim().equals("")) {
      throw new DAOException("Cannot lookup null or empty pet");
    }

    amazonDynamoDBMapper.save(pet);

    return pet.getPetId();
  }

  /**
   * Gets a Pet by its id
   *
   * @param petId The petId to look for
   * @return An initialized Pet object, null if the Pet could not be found
   */
  public Pet getPetById(String petId) throws DAOException {
    if (petId == null || petId.trim().equals("")) {
      throw new DAOException("Cannot lookup null or empty petId");
    }

    return amazonDynamoDBMapper.load(Pet.class, petId);
  }

  /**
   * Returns a list of pets in the DynamoDB table.
   *
   * @param limit The maximum numbers of results for the scan
   * @return A List of Pet objects
   */
  public List<Pet> getPets(int limit) {
    if (limit <= 0 || limit > DynamoDBConfiguration.SCAN_LIMIT)
      limit = DynamoDBConfiguration.SCAN_LIMIT;

    DynamoDBScanExpression expression = new DynamoDBScanExpression();
    expression.setLimit(limit);
    return amazonDynamoDBMapper.scan(Pet.class, expression);
  }
}
