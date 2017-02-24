package cloud.benchflow.performancetestorchestrator.services.external;

import cloud.benchflow.performancetestorchestrator.constants.BenchFlowConstants;
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

    public void savePerformanceTestDefinition(String performanceTestID, InputStream definition) {

        logger.info("savePerformanceTestDefinition: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

        putInputStreamObject(definition, objectName);

    }

    public InputStream getPerformanceTestDefinition(String performanceTestID) {

        logger.info("getPerformanceTestDefinition: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

        return getInputStreamObject(objectName);

    }

    public void removePerformanceTestDefinition(String performanceTestID) {

        logger.info("removePerformanceTestDefinition: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

        removeObject(objectName);

    }

    public void savePerformanceTestDeploymentDescriptor(String performanceTestID, InputStream deploymentDescriptor) {

        logger.info("savePerformanceTestDeploymentDescriptor: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        putInputStreamObject(deploymentDescriptor, objectName);

    }

    public InputStream getPerformanceTestDeploymentDescriptor(String performanceTestID) {

        logger.info("getPerformanceTestDeploymentDescriptor: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        return getInputStreamObject(objectName);

    }

    public void removePerformanceTestDeploymentDescriptor(String performanceTestID) {

        logger.info("removePerformanceTestDeploymentDescriptor: " + performanceTestID);

        String objectName = minioCompatibleID(performanceTestID) + "/" + BenchFlowConstants.DEPLOYMENT_DESCRIPTOR_FILE_NAME;

        removeObject(objectName);

    }

    public void savePerformanceTestBPMNModel(String performanceTestID, String modelName, InputStream model) {

        logger.info("savePerformanceTestBPMNModel: " + performanceTestID + "/" + modelName);

        String objectName = minioCompatibleID(performanceTestID) + "/" + modelName;

        putInputStreamObject(model, objectName);

    }

    public InputStream getPerformanceTestBPMNModel(String performanceTestID, String modelName) {

        logger.info("getPerformanceTestBPMNModel: " + performanceTestID + "/" + modelName);

        String objectName = minioCompatibleID(performanceTestID) + "/" + modelName;

        return getInputStreamObject(objectName);

    }

    public void removePerformanceTestBPMNModel(String performanceTestID, String modelName) {

        logger.info("removePerformanceTestBPMNModel: " + performanceTestID + "/" + modelName);

        String objectName = minioCompatibleID(performanceTestID) + "/" + modelName;

        removeObject(objectName);

    }

    public void savePerformanceExperimentDefinition(String performanceExperimentID, InputStream definition) {

        logger.info("savePerformanceExperimentDefinition: " + performanceExperimentID);

        String objectName = minioCompatibleID(performanceExperimentID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

        putInputStreamObject(definition, objectName);

    }

    public InputStream getPerformanceExperimentDefinition(String performanceExperimentID) {

        logger.info("getPerformanceExperimentDefinition: " + performanceExperimentID);

        String objectName = minioCompatibleID(performanceExperimentID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

        return getInputStreamObject(objectName);
    }

    public void removePerformanceExperimentDefinition(String performanceExperimentID) {

        logger.info("removePerformanceExperimentDefinition: " + performanceExperimentID);

        String objectName = minioCompatibleID(performanceExperimentID) + "/" + BenchFlowConstants.PT_PE_DEFINITION_FILE_NAME;

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
