package cloud.benchflow.testmanager.services.external;

import cloud.benchflow.testmanager.constants.BenchFlowConstants;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 18.12.16.
 */
public class MinioService {

    // http://www.iana.org/assignments/media-types/application/octet-stream
    private static final String CONTENT_TYPE = "application/octet-stream";
    private static Logger logger = LoggerFactory.getLogger(MinioService.class.getSimpleName());
    private MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void initializeBuckets() {

        try {
            if (!minioClient.bucketExists(BenchFlowConstants.TESTS_BUCKET)) {
                minioClient.makeBucket(BenchFlowConstants.TESTS_BUCKET);
            }

        } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException | InsufficientDataException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException e) {
            // TODO - handle exception
            logger.error("Exception in initializeBuckets ", e);
        }

    }

    public void saveTestDefinition(String testID, InputStream definition) {

        logger.info("saveTestDefinition: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        putInputStreamObject(definition, objectName);

    }

    public InputStream getTestDefinition(String testID) {

        logger.info("getTestDefinition: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        return getInputStreamObject(objectName);

    }

    public void removeTestDefinition(String testID) {

        logger.info("removeTestDefinition: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        removeObject(objectName);

    }

    public void saveTestDeploymentDescriptor(String testID, InputStream deploymentDescriptor) {

        logger.info("saveTestDeploymentDescriptor: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        putInputStreamObject(deploymentDescriptor, objectName);

    }

    public InputStream getTestDeploymentDescriptor(String testID) {

        logger.info("getTestDeploymentDescriptor: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        return getInputStreamObject(objectName);

    }

    public void removeTestDeploymentDescriptor(String testID) {

        logger.info("removeTestDeploymentDescriptor: " + testID);

        String objectName = minioCompatibleID(testID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        removeObject(objectName);

    }

    public void saveTestBPMNModel(String testID, String modelName, InputStream model) {

        logger.info("saveTestBPMNModel: " + testID + "/" + modelName);

        String objectName = minioCompatibleID(testID) + "/" + modelName;

        putInputStreamObject(model, objectName);

    }

    public InputStream getTestBPMNModel(String testID, String modelName) {

        logger.info("getTestBPMNModel: " + testID + "/" + modelName);

        String objectName = minioCompatibleID(testID) + "/" + modelName;

        return getInputStreamObject(objectName);

    }

    public void removeTestBPMNModel(String testID, String modelName) {

        logger.info("removeTestBPMNModel: " + testID + "/" + modelName);

        String objectName = minioCompatibleID(testID) + "/" + modelName;

        removeObject(objectName);

    }

    public void saveExperimentDefinition(String experimentID, InputStream definition) {

        logger.info("saveExperimentDefinition: " + experimentID);

        String objectName = minioCompatibleID(experimentID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        putInputStreamObject(definition, objectName);

    }

    public InputStream getExperimentDefinition(String experimentID) {

        logger.info("getExperimentDefinition: " + experimentID);

        String objectName = minioCompatibleID(experimentID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        return getInputStreamObject(objectName);
    }

    public void removeExperimentDefinition(String experimentID) {

        logger.info("removeExperimentDefinition: " + experimentID);

        String objectName = minioCompatibleID(experimentID) + "/" + BenchFlowConstants.TEST_EXPERIMENT_DEFINITION_FILE_NAME;

        removeObject(objectName);
    }

    /**
     *
     * @param inputStream
     * @param objectName
     */
    private void putInputStreamObject(InputStream inputStream, String objectName) {

        logger.info("putInputStreamObject: " + objectName);

        try {

            minioClient.putObject(BenchFlowConstants.TESTS_BUCKET,
                                  objectName,
                                  inputStream,
                                  inputStream.available(),
                                  CONTENT_TYPE);

        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | NoResponseException | InvalidKeyException | ErrorResponseException | XmlPullParserException | InvalidArgumentException | InternalException e) {
            // TODO - handle exception
            logger.error("Exception in putInputStreamObject: " + objectName, e);
        }
    }

    /**
     *
     * @param objectName
     * @return
     */
    private InputStream getInputStreamObject(String objectName) {

        logger.info("getInputStreamObject: " + objectName);

        try {

            return minioClient.getObject(BenchFlowConstants.TESTS_BUCKET, objectName);

        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | InternalException | InvalidArgumentException e) {
            // TODO - handle exception
            logger.error("Exception in getInputStreamObject: " + objectName, e);
            return null;

        } catch (ErrorResponseException e) {
           /* happens if the object doesn't exist*/
           return null;
        }
    }

    private void removeObject(String objectName) {

        logger.info("removeObject: " + objectName);

        try {
            minioClient.removeObject(BenchFlowConstants.TESTS_BUCKET, objectName);
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | InternalException e) {
            // TODO - handle exception
            logger.error("Exception in removeObject: " + objectName, e);
        } catch (ErrorResponseException e) {
            /* happens if the object to remove doesn't exist, do nothing */
        }


    }

    private String minioCompatibleID(String id) {
        return id.replace(".", "/");
    }


}
