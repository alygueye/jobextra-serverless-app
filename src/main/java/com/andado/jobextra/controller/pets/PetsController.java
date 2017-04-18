
package com.andado.jobextra.controller.pets;

import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequest;
import com.amazonaws.util.StringUtils;
import com.andado.jobextra.configuration.DynamoDBConfiguration;
import com.andado.jobextra.controller.BaseController;
import com.andado.jobextra.dao.pets.PetDao;
import com.andado.jobextra.exception.BadRequestException;
import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.exception.ExceptionMessages;
import com.andado.jobextra.exception.InternalErrorException;
import com.andado.jobextra.model.pets.Pet;
import com.andado.jobextra.pojo.ListResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

@RestController
//@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RequestMapping(value = "/api/jobextra", produces = {"application/json; charset=UTF-8"})
@EnableWebMvc
public class PetsController extends BaseController {

  // Initialize the Log4j logger.
  static final Logger LOG = Logger.getLogger(PetsController.class);

  @Autowired
  Validator validator;

  @Autowired
  private PetDao petDao;

  @RequestMapping(path = "/pets", method = RequestMethod.POST)
  public String createPet(@RequestBody Pet input, AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (input == null || input.getPetType() == null || input.getPetType().trim().equals("")) {
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }

    String petId;

    try {
      petId = petDao.createPet(input);
    } catch (final DAOException e) {
      LOG.error("Error while creating new pet\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }

    if (petId == null || petId.trim().equals("")) {
      LOG.error("PetID is null or empty");
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    return petId;
  }

  @RequestMapping(path = "/pets", method = RequestMethod.GET)
  public ListResponse<Pet> listPets(@RequestParam("limit") Optional<Integer> limit, AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    int pageLimit = limit.orElse(DynamoDBConfiguration.SCAN_LIMIT);
    List<Pet> pets = petDao.getPets(pageLimit);
    ListResponse<Pet> output = new ListResponse<>();
    output.setCount(pets.size());
    output.setPageLimit(pageLimit);
    output.setResult(pets);
    return output;
  }

  @RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
  public Pet get(@PathVariable(name = "petId") String petId, AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (StringUtils.isNullOrEmpty(petId)) {
      LOG.error("Invalid input passed ");
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }

    Pet pet;
    try {
      pet = petDao.getPetById(petId);
    } catch (final DAOException e) {
      LOG.error("Error while fetching pet with id " + petId + "\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    return pet;
  }

}
