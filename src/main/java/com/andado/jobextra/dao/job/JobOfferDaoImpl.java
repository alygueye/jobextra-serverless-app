package com.andado.jobextra.dao.job;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.util.StringUtils;
import com.andado.jobextra.configuration.DynamoDBConfiguration;
import com.andado.jobextra.exception.DAOException;
import com.andado.jobextra.model.job.JobOffer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The DynamoDB implementation of the JobOffer object.
 */
@Component
public class JobOfferDaoImpl implements JobOfferDao {

  // Initialize the Log4j logger.
  static final Logger LOG = Logger.getLogger(JobOfferDaoImpl.class);

  static SimpleDateFormat dateFormatter = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @Autowired
  private DynamoDBMapper amazonDynamoDBMapper;


  /**
   * {@inheritDoc}
   */
  @Override
  public JobOffer createJob(JobOffer job) throws DAOException {
    if (StringUtils.isNullOrEmpty(job.getPostedBy())) {
      throw new DAOException("userId must be specified");
    }
    if (job.getCreationDateTime() == null) {
      job.setCreationDateTime(new Date());
    }
    amazonDynamoDBMapper.save(job);

    return job;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JobOffer getJobById(String userId, Date creationDateTime) throws DAOException {
    if (StringUtils.isNullOrEmpty(userId) || creationDateTime == null) {
      throw new DAOException("Cannot lookup null or empty userId/creationDateTime");
    }

    return amazonDynamoDBMapper.load(JobOffer.class, userId, creationDateTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<JobOffer> getJobByUser(String userId, int limit) throws DAOException {
    if (StringUtils.isNullOrEmpty(userId)) {
      throw new DAOException("Cannot lookup null or empty userId");
    }
    if (limit <= 0 || limit > DynamoDBConfiguration.SCAN_LIMIT)
      limit = DynamoDBConfiguration.SCAN_LIMIT;
    JobOffer jobOfferKey = new JobOffer();
    jobOfferKey.setPostedBy(userId);
    DynamoDBQueryExpression<JobOffer> queryExpression = new DynamoDBQueryExpression<JobOffer>()
      .withHashKeyValues(jobOfferKey);
    queryExpression.setLimit(limit);
    List<JobOffer> jobs = amazonDynamoDBMapper.query(JobOffer.class, queryExpression);
    return jobs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<JobOffer> getJobByUserInLastNDays(String userId, long nDays, int limit) throws DAOException {
    if (limit <= 0 || limit > DynamoDBConfiguration.SCAN_LIMIT)
      limit = DynamoDBConfiguration.SCAN_LIMIT;
    if (nDays <= 0)
      nDays = DynamoDBConfiguration.DEFAULT_NB_DAY;

    long nDaysAgoMilli = (new Date()).getTime() - (nDays*24L*60L*60L*1000L);
    Date nDaysAgo = new Date();
    nDaysAgo.setTime(nDaysAgoMilli);
    Condition rangeKeyCondition = new Condition()
      .withComparisonOperator(ComparisonOperator.GT.toString())
      .withAttributeValueList(new AttributeValue().withN(StringUtils.fromLong(nDaysAgo.getTime())));

    JobOffer jobOfferKey = new JobOffer();
    jobOfferKey.setPostedBy(userId);
    DynamoDBQueryExpression<JobOffer> queryExpression = new DynamoDBQueryExpression<JobOffer>()
      .withHashKeyValues(jobOfferKey)
      .withRangeKeyCondition("creationDateTime", rangeKeyCondition);
    queryExpression.setLimit(limit);
    List<JobOffer> jobs = amazonDynamoDBMapper.query(JobOffer.class, queryExpression);
    return jobs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<JobOffer> getJobs(int limit) {
    if (limit <= 0 || limit > DynamoDBConfiguration.SCAN_LIMIT)
      limit = DynamoDBConfiguration.SCAN_LIMIT;

    DynamoDBScanExpression expression = new DynamoDBScanExpression();
    expression.setLimit(limit);
    return amazonDynamoDBMapper.scan(JobOffer.class, expression);
  }

}
