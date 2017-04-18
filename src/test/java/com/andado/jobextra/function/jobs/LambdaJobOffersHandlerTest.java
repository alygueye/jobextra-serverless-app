package com.andado.jobextra.function.jobs;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.andado.jobextra.LocalDynamoDBCreationRule;
import com.andado.jobextra.function.LambdaJobHandler;
import com.andado.jobextra.function.TestBase;
import com.andado.jobextra.model.job.JobOffer;
import com.andado.jobextra.pojo.ListResponse;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Date;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(JUnit4.class)
public class LambdaJobOffersHandlerTest extends TestBase {

  // Initialize the Log4j logger.
  static final Logger LOGGER = Logger.getLogger(LambdaJobOffersHandlerTest.class);

  private static AwsProxyRequest getAll;
  private static AwsProxyRequest postOne;
  private static AwsProxyRequest getByUserAndDate;
  private static AwsProxyRequest getByUser;

  @ClassRule
  public static final LocalDynamoDBCreationRule dynamoDB = new LocalDynamoDBCreationRule(JobOffer.class);

  @BeforeClass
  public static void createInput() throws IOException {
    // TODO: set up your sample input object here.
    getAll = getGson().fromJson(readResource("ScanJobOffers.json"), AwsProxyRequest.class);
    postOne = getGson().fromJson(readResource("PostJobOffer.json"), AwsProxyRequest.class);
    getByUserAndDate = getGson().fromJson(readResource("GetJobOffersByUserAndDate.json"), AwsProxyRequest.class);
    getByUser = getGson().fromJson(readResource("GetJobOffersByUser.json"), AwsProxyRequest.class);
  }

  private Context createContext() {
    TestJobOfferContext ctx = new TestJobOfferContext();

    // TODO: customize your context here if needed.
    ctx.setFunctionName("Your Function Name");

    return ctx;
  }

  @Test
  public void testHello() {
    LambdaJobHandler handler = new LambdaJobHandler();
    Context ctx = createContext();
    JobOffer data=new JobOffer();
    data.setPostedBy("uuuu2777");
    data.setCreationDateTime(new Date());
    data.setTitle("test");
    dynamoDB.getDynamoDBMapper().save(data);

    AwsProxyResponse output;
    //Create a job
    output=handler.handleRequest(postOne, ctx);
    LOGGER.info(output.getBody().toString());
    JobOffer job=getGson().fromJson(output.getBody(),JobOffer.class);
    Assert.assertNotNull(job);
    Assert.assertEquals(job.getPostedBy(),"uuuu2777");
    Assert.assertEquals(job.getCreationDateTime().getTime(),1492389582824L);

    //Find by hash+range
    output=handler.handleRequest(getByUserAndDate, ctx);
    LOGGER.info(output.getBody().toString());
    job=getGson().fromJson(output.getBody(),JobOffer.class);
    Assert.assertNotNull(job);
    Assert.assertEquals(job.getPostedBy(),"uuuu2777");
    Assert.assertEquals(job.getCreationDateTime().getTime(),1492389582824L);

    //Find by hash
    output=handler.handleRequest(getByUser, ctx);
    LOGGER.info(output.getBody().toString());
    ListResponse<JobOffer> jobs=getGson().fromJson(output.getBody(),ListResponse.class);
    Assert.assertNotNull(jobs);
    Assert.assertEquals(jobs.getCount(),2);

    //Retrieve all jobs by scan
     output = handler.handleRequest(getAll, ctx);
    if (output != null) {
      LOGGER.info(output.getBody().toString());
    }
    ListResponse<JobOffer> result=getGson().fromJson(output.getBody(),ListResponse.class);
    Assert.assertEquals(result.getCount(),2);

  }
}
