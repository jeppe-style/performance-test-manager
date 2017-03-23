package cloud.benchflow.performancetestmanager.resources;

import cloud.benchflow.performancetestmanager.api.response.GetPerformanceTestStatusResponse;
import cloud.benchflow.performancetestmanager.exceptions.PerformanceTestIDDoesNotExistException;
import cloud.benchflow.performancetestmanager.exceptions.web.InvalidPerformanceTestIDWebException;
import cloud.benchflow.performancetestmanager.helpers.TestConstants;
import cloud.benchflow.performancetestmanager.models.BenchFlowTestModel;
import cloud.benchflow.performancetestmanager.services.internal.dao.PerformanceTestModelDAO;
import cloud.benchflow.performancetestmanager.constants.BenchFlowConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 19.12.16.
 */
public class PerformanceTestStatusResourceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private PerformanceTestModelDAO daoMock = Mockito.mock(PerformanceTestModelDAO.class);

    private PerformanceTestStatusResource statusResource;

    @Before
    public void setUp() throws Exception {

        statusResource = new PerformanceTestStatusResource(daoMock);

    }

    @Test
    public void getPerformanceTestStatusInValid() throws Exception {

        String performanceTestID = "inValid";

        doThrow(PerformanceTestIDDoesNotExistException.class).when(daoMock).getPerformanceTestModel(performanceTestID);

        exception.expect(InvalidPerformanceTestIDWebException.class);

        statusResource.getPerformanceTestStatus(performanceTestID);

        verify(daoMock, times(1)).getPerformanceTestModel(performanceTestID);

    }

    @Test
    public void getPerformanceTestStatusValid() throws Exception {

        String performanceTestName = TestConstants.VALID_PERFORMANCE_TEST_NAME;

        String expectedPerformanceTestID = TestConstants.TEST_USER_NAME + BenchFlowConstants.MODEL_ID_DELIMITER + performanceTestName + BenchFlowConstants.MODEL_ID_DELIMITER + 1;

        doReturn(new BenchFlowTestModel(TestConstants.TEST_USER, performanceTestName, 1)).when(
                daoMock).getPerformanceTestModel(performanceTestName);

        GetPerformanceTestStatusResponse response = statusResource.getPerformanceTestStatus(performanceTestName);

        verify(daoMock, times(1)).getPerformanceTestModel(performanceTestName);

        // TODO - decide what status should contain and make assertions accordingly

        Assert.assertNotNull(response);
        Assert.assertEquals(expectedPerformanceTestID, response.getPerformanceTestID());

    }


}