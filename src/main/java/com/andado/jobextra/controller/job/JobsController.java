
package com.andado.jobextra.controller.job;

import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequest;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import com.andado.jobextra.configuration.DynamoDBConfiguration;
import com.andado.jobextra.controller.BaseController;
import com.andado.jobextra.dao.job.JobOfferDao;
import com.andado.jobextra.exception.BadRequestException;
import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.exception.ExceptionMessages;
import com.andado.jobextra.exception.InternalErrorException;
import com.andado.jobextra.model.job.JobOffer;
import com.andado.jobextra.model.pets.Pet;
import com.andado.jobextra.pojo.ListResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

@RestController
//@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@RequestMapping(value = "/api/jobs", produces = {"application/json; charset=UTF-8"})
@EnableWebMvc
public class JobsController extends BaseController {

  // Initialize the Log4j logger.
  static final Logger LOG = Logger.getLogger(JobsController.class);

  @Autowired
  Validator validator;

  @Autowired
  private JobOfferDao jobOfferDao;

  @RequestMapping(path = "/users/{userId}", method = RequestMethod.GET)
  public ListResponse<JobOffer> getByUser(@PathVariable(name = "userId") String userId,
                                          @RequestParam("limit") Optional<Integer> limit,
                                          AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (StringUtils.isNullOrEmpty(userId)) {
      LOG.error("Invalid input passed ");
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }
    int pageLimit = limit.orElse(DynamoDBConfiguration.SCAN_LIMIT);
    ListResponse<JobOffer> output = new ListResponse();
    List<JobOffer> jobs;
    try {
      jobs = jobOfferDao.getJobByUser(userId,pageLimit);
    } catch (final DAOException e) {
      LOG.error("Error while fetching jobs for user id " + userId + "\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    if(CollectionUtils.isNullOrEmpty(jobs)){
      return output;
    }
    output.setCount(jobs.size());
    output.setResult(jobs);
    output.setPageLimit(pageLimit);
    return output;
  }
  @RequestMapping(path = "/users/{userId}/latest", method = RequestMethod.GET)
  public ListResponse<JobOffer> getByUserInLastNDays(@PathVariable(name = "userId") String userId,
                                                     @RequestParam("nDays") Optional<Long> nDays,
                                                     @RequestParam("limit") Optional<Integer> limit,
                                                     AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (StringUtils.isNullOrEmpty(userId)) {
      LOG.error("Invalid input passed ");
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }
    int pageLimit = limit.orElse(DynamoDBConfiguration.SCAN_LIMIT);
    ListResponse<JobOffer> output = new ListResponse();
    List<JobOffer> jobs;
    try {
      jobs = jobOfferDao.getJobByUserInLastNDays(userId,nDays.orElse(DynamoDBConfiguration.DEFAULT_NB_DAY),pageLimit);
    } catch (final DAOException e) {
      LOG.error("Error while fetching jobs for user id " + userId + "\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    if(CollectionUtils.isNullOrEmpty(jobs)){
      return output;
    }
    output.setCount(jobs.size());
    output.setResult(jobs);
    output.setPageLimit(pageLimit);
    return output;
  }

  @RequestMapping(path = "/users/{userId}", method = RequestMethod.POST)
  public JobOffer createJob(@PathVariable(name = "userId") String userId,
                            @RequestBody JobOffer input,
                            AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (input == null /*|| (StringUtils.isNullOrEmpty(input.getPostedBy())&& StringUtils.isNullOrEmpty(userId))*/) {
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }
    input.setPostedBy(userId);
    JobOffer result;

    try {
      result = jobOfferDao.createJob(input);
    } catch (final DAOException e) {
      LOG.error("Error while creating new job\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    return result;
  }

  @RequestMapping(path = "/list", method = RequestMethod.GET)
  public ListResponse<JobOffer> scanJobs(@RequestParam("limit") Optional<Integer> limit,
                                         AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    int pageLimit = limit.orElse(DynamoDBConfiguration.SCAN_LIMIT);
    List<JobOffer> jobs = jobOfferDao.getJobs(pageLimit);
    ListResponse<JobOffer> output = new ListResponse();
    output.setCount(jobs.size());
    output.setPageLimit(pageLimit);
    output.setResult(jobs);
    return output;
  }

  @RequestMapping(path = {"/list/{userId}/{creationDateTime}","/users/{userId}/{creationDateTime}"}, method = RequestMethod.GET)
  public JobOffer getByKeys(@PathVariable(name = "userId") String userId,
                                   @PathVariable(name = "creationDateTime") long timeStamp,
                                   AwsProxyHttpServletRequest request) throws Exception {
    LOG.debug(logRequest(request));

    if (StringUtils.isNullOrEmpty(userId)|| timeStamp<=0) {
      LOG.error("Invalid input passed ");
      throw new BadRequestException(ExceptionMessages.EX_INVALID_INPUT);
    }
    JobOffer job;
    try {
      job = jobOfferDao.getJobById(userId,new Date(timeStamp));
    } catch (final DAOException e) {
      LOG.error("Error while fetching jobs for user id " + userId + " and creationDateTime " +new Date(timeStamp).toString()+"\n" + e.getMessage());
      throw new InternalErrorException(ExceptionMessages.EX_DAO_ERROR);
    }
    return job;
  }

}
