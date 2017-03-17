/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.andado.jobextra;

import com.amazonaws.serverless.proxy.internal.RequestReader;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.andado.jobextra.model.pets.Pet;
import com.andado.jobextra.model.pets.PetDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;

@RestController
@EnableWebMvc
public class PetsController {

  private LambdaLogger logger;

  @Autowired
  private PetDAO petDAO;

  @RequestMapping(path = "/pets", method = RequestMethod.POST)
  public Pet createPet(@RequestBody Pet newPet, AwsProxyHttpServletRequest containerRequest) {
    Context lambdaContext = (Context) containerRequest.getAttribute(RequestReader.LAMBDA_CONTEXT_PROPERTY);
    logger = lambdaContext.getLogger();
    logger.log(lambdaContext.toString());
    logger.log(containerRequest.toString());

    return null;
  }

  @RequestMapping(path = "/pets", method = RequestMethod.GET)
  public Pet[] listPets(@RequestParam("limit") Optional<Integer> limit, AwsProxyHttpServletRequest containerRequest) {

    Pet[] outputPets = new Pet[0];
    Context lambdaContext = (Context) containerRequest.getAttribute(RequestReader.LAMBDA_CONTEXT_PROPERTY);

    return outputPets;
  }

  @RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
  public Pet listPets(AwsProxyHttpServletRequest containerRequest) {
    Pet newPet = new Pet();
    return newPet;
  }

}
