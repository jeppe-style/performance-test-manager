package cloud.benchflow.testmanager.tasks;

/**
 * @author Jesper Findahl (jesper.findahl@usi.ch)
 *         created on 2017-04-06
 */
public class RunBenchFlowDemoTestTaskTest {

//    private static final String DEMO_TEST_ARCHIVE_FILENAME = "src/test/resources/data/ParallelMultiple11.zip";
//
//    private RunBenchFlowDemoTestTask demoTestTask;
//    private String testID;
//
//    // Mocks
//    private MinioService minioServiceMock = Mockito.mock(MinioService.class);
//    private BenchFlowExperimentManagerService experimentManagerServiceMock = Mockito.mock(BenchFlowExperimentManagerService.class);
//    private BenchFlowExperimentModelDAO experimentModelDAOMock = Mockito.mock(BenchFlowExperimentModelDAO.class);
//
//
//    @Before
//    public void setUp() throws Exception {
//
//        testID = TestConstants.TEST_USER_NAME + MODEL_ID_DELIMITER + "ParallelMultiple11Activiti5210" + MODEL_ID_DELIMITER + 1;
//
//        ZipInputStream demoZipArchive = new ZipInputStream(new FileInputStream(DEMO_TEST_ARCHIVE_FILENAME));
//
//        String testDefinitionYamlString = BenchFlowTestArchiveExtractor.extractBenchFlowTestDefinitionString(
//                demoZipArchive);
//
//        InputStream deploymentDescriptorInputStream = BenchFlowTestArchiveExtractor.extractDeploymentDescriptorInputStream(
//                demoZipArchive);
//        Map<String, InputStream> bpmnModelsInputStream = BenchFlowTestArchiveExtractor.extractBPMNModelInputStreams(
//                demoZipArchive);
//
//
//        demoTestTask = new RunBenchFlowDemoTestTask(
//                testID,
//                testDefinitionYamlString,
//                deploymentDescriptorInputStream,
//                bpmnModelsInputStream,
//                minioServiceMock,
//                experimentManagerServiceMock,
//                experimentModelDAOMock
//        );
//
//    }
//
//    @Test
//    public void run() throws Exception {
//
//        String experimentID = testID + MODEL_ID_DELIMITER + "1";
//
//        Mockito.doReturn(experimentID).when(experimentModelDAOMock).addExperiment(testID);
//
//        demoTestTask.run();
//
//    }

}