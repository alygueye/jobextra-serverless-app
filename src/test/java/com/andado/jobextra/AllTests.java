package com.andado.jobextra;

import com.andado.jobextra.function.jobs.LambdaJobOffersHandlerTest;
import com.andado.jobextra.function.pets.LambdaPetsHandlerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({LambdaPetsHandlerTest.class, LambdaJobOffersHandlerTest.class})
public class AllTests {
}
