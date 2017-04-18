
package com.andado.jobextra.dao.job;


import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.model.job.JobOffer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * This interface defines the methods required for an implementation of the JobOfferDao object
 */
public interface JobOfferDao {
  /**
   * Creates a new jobOffer in the data store
   *
   * @param job The jobOffer object to be created
   * @return The generated jobOffer
   * @throws DAOException Whenever an error occurs while accessing the data store
   */
  JobOffer createJob(JobOffer job) throws DAOException;

  /**
   * Retrieves a job object by id(hash+range keys)
   *
   * @param userId The userId who post the job offer
   * @param creationDateTime creation Date Time
   * @return An initialized and populated JobOffer object
   * @throws DAOException Whenever a data store access error occurs
   */
  JobOffer getJobById(String userId, Date creationDateTime) throws DAOException;

  /**
   * Retrieves a job object by its user
   *
   * @param userId The userId who post the job offer
   * @return A list of initialized and populated JobOffer object
   * @throws DAOException Whenever a data store access error occurs
   */
  List<JobOffer> getJobByUser(String userId,int limit) throws DAOException;

  /**
   * Retrieves a jobs object by its user for the last nDays
   *
   * @param userId The userId who post the job offer
   * @param nDays nb last days
   * @param limit result limit
   * @return A list of initialized and populated JobOffer object
   * @throws DAOException Whenever a data store access error occurs
   */
  List<JobOffer> getJobByUserInLastNDays(String userId,long nDays,int limit) throws DAOException;



  List<JobOffer> getJobs(int limit);
}
