'use strict';
var assert = require('assert');

var defaults = {
  AWS_PROFILE: 'default',
  AWS_REGION: 'us-east-1',
  ENVIRONMENT_STAGE: 'dev',
  PROJECT_PREFIX: 'jobextra-api-',
  PACKAGE_VERSION: '1.0.0',
  MAVEN_PROJECT_VERSION: '1.0-SNAPSHOT',
  MAVEN_ARTIFACT_NAME: 'jobextra-serverless-app'
};

function getVar(name) {
  if (process.env[name]){
    console.log('Getting value', name, 'from environmental variable with value', process.env[name], ' overriding ', defaults[name]);
    return process.env[name];
  }
  return defaults[name];
}

var config = {
  AWS_PROFILE: getVar('AWS_PROFILE'),
  AWS_REGION: getVar('AWS_REGION'),
  ENVIRONMENT_STAGE: getVar('ENVIRONMENT_STAGE'),
  PROJECT_PREFIX: getVar('PROJECT_PREFIX'),
  PACKAGE_VERSION: getVar('PACKAGE_VERSION'),
  JAR_NAME: getVar('MAVEN_ARTIFACT_NAME'),
  JAR_VERSION: getVar('MAVEN_PROJECT_VERSION')
};

// For local development, define these properties before requiring the SDK since it will provide the right credentials
if (!process.env.LAMBDA_TASK_ROOT) {
  // Code is not running in a Lambda container, set AWS profile to use
  process.env.AWS_PROFILE = config.AWS_PROFILE;
  process.env.AWS_REGION = config.AWS_REGION;
}

// Attach this AWS object with credentials setup for other methods.
config.AWS = require('aws-sdk');
config.getName = (suffix) => {
  return config.getResourcePrefix() + suffix;
};
config.getResourcePrefix = () => {
  return config.PROJECT_PREFIX + config.ENVIRONMENT_STAGE + '-';
};
config.getLambdaZipName = () => {
  return 'lambda-' + config.PACKAGE_VERSION + '.zip';
};
config.getLambdaJavaJarName = () => {
  return config.JAR_NAME +'-'+ config.JAR_VERSION + '.jar';
};
config.getLambdaZipPath = () => {
  var path = require('path');
  return path.join(__dirname, 'dist', 'lambda-' + config.PACKAGE_VERSION + '.zip');
};
config.getLambdaJavaJarPath = () => {
    var path = require('path');
    return path.join(__dirname, '..', 'target', config.JAR_NAME + '-' + config.JAR_VERSION + '.jar');
};

config.getFunctionsConfigParams = (cfOutputs) => {
  assert(cfOutputs.LambdaExecutionRoleArn, 'Missing LambdaExecutionRoleArn');
  return {
    petsController: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaJavaJarName(),
      },
      Description: 'Pets Controller',
      FunctionName: config.getName('pets-Controller'),
      Handler: 'com.andado.jobextra.function.LambdaPetsHandler::handleRequest',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'java8',
      MemorySize: 512,
      Timeout: 20
      /*Environment: {
        Variables: {
          someKey: config.ENVIRONMENT_STAGE,

        }
      }*/
    },
    jobsController: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaJavaJarName(),
      },
      Description: 'Jobs Controller',
      FunctionName: config.getName('jobs-Controller'),
      Handler: 'com.andado.jobextra.function.LambdaJobHandler::handleRequest',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'java8',
      MemorySize: 512,
      Timeout: 20
      /*Environment: {
       Variables: {
       someKey: config.ENVIRONMENT_STAGE,

       }
       }*/
    },
    locationsList: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Returns List of Locations',
      FunctionName: config.getName('locations-List'),
      Handler: 'locations.List',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    locationsCreate: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Adds a Location',
      FunctionName: config.getName('locations-Create'),
      Handler: 'locations.Create',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    locationsDelete: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Deletes a Location',
      FunctionName: config.getName('locations-Delete'),
      Handler: 'locations.Delete',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    locationsGet: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Returns a Location',
      FunctionName: config.getName('locations-Get'),
      Handler: 'locations.Get',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    resourcesList: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Returns list of Resources',
      FunctionName: config.getName('resources-List'),
      Handler: 'resources.List',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    resourcesCreate: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Add Resource',
      FunctionName: config.getName('resources-Create'),
      Handler: 'resources.Create',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },

    resourcesGet: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Get resource',
      FunctionName: config.getName('resources-Get'),
      Handler: 'resources.Get',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    resourcesDelete: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Deletes a Resource',
      FunctionName: config.getName('resources-Delete'),
      Handler: 'resources.Delete',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    bookingsListByResourceId: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Returns List of Bookings associated with Resource',
      FunctionName: config.getName('bookings-ListByResourceId'),
      Handler: 'bookings.ListByResourceId',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    bookingsListByUserId: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Returns List of Bookings associated with a User',
      FunctionName: config.getName('bookings-ListByUserId'),
      Handler: 'bookings.ListByUserId',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    bookingsCreate: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Add Booking',
      FunctionName: config.getName('bookings-Create'),
      Handler: 'bookings.Create',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    bookingsGet: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Get booking',
      FunctionName: config.getName('bookings-Get'),
      Handler: 'bookings.Get',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    },
    bookingsDelete: {
      Code: {
        S3Bucket: cfOutputs.LambdaBucket,
        S3Key: config.getLambdaZipName(),
      },
      Description: 'Delete booking',
      FunctionName: config.getName('bookings-Delete'),
      Handler: 'bookings.Delete',
      Role: cfOutputs.LambdaExecutionRoleArn,
      Runtime: 'nodejs4.3',
      Timeout: 10
    }

  };
};

module.exports = config;
