package eu.uqasar.testlink.adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.Execution;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;



public class TestlinkAdapterTest {
//	//maquetas.mtp.es
	final String DEV_KEY = "eff56abd1df91d9eeaca2eb6ead9cfec";
	final String SERVER_URL = "http://maquetas.mtp.es/testlink/lib/api/xmlrpc/v1/xmlrpc.php";
	
    TestLinkAdapter adapter;
	
	@Before
	public void setUp() {
		
		try {
			adapter = new TestLinkAdapter(SERVER_URL, DEV_KEY);
		} catch (uQasarException e) {
			System.out.println("\n***setUp***\n" + e.getMessage());
		}
	}
	
	@Test
	public void testApiContructor() {
		System.out.println("\n***testApiContructor***\n");
		
		TestLinkAdapter apiTL;
		try {
			apiTL  = new TestLinkAdapter("maquetas.mtp.es", "");
		} catch (uQasarException e) {
			Assert.assertNotNull(e.getMessage());
		}
	}	
	
	@Test
	public void testgetTestProjectByNameKO(){
		System.out.println("\n***testgetTestProjectByNameKO***\n");

		TestProject obj = adapter.getTestProjectByName("aaa");
		Assert.assertNull(obj);
	}
	
	@Test
	public void testgetProjectTestPlans() {
		System.out.println("\n***testgetProjectTestPlans***\n");

		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);
		
		TestPlan[] obj = adapter.getProjectTestPlans(result[0].getId());
		Assert.assertNotNull(obj);
	}
	
	@Test
	public void testgetProjectTestPlansKO() {
		System.out.println("\n***testgetProjectTestPlansKO***\n");

		TestPlan[] obj = adapter.getProjectTestPlans(0);
		Assert.assertNull(obj);
	}
	
	@Test
	public void testgetTestPlanByNameKO() {
		System.out.println("\n***testgetTestPlanByNameKO***\n");

		TestPlan tp = adapter.getTestPlanByName("0", "0");
		Assert.assertNull(tp);
	}	


	@Test
	public void testgetTestSuiteByID() {
		System.out.println("\n***testgetTestSuiteByID***\n");

		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);
		
		TestPlan[] obj = adapter.getProjectTestPlans(result[0].getId());
		Assert.assertNotNull(obj);

		TestSuite[] ts = adapter.getFirstLevelTestSuitesForTestProject(result[0].getId());
		
		List<Integer> ids = new ArrayList<Integer>();
		for (TestSuite suite : ts) {
			ids.add(suite.getId());
		}
		
		TestSuite[] list = adapter.getTestSuiteByID (ids);
		//Assert.assertArrayEquals(ts, list); not equal due to order
		Assert.assertEquals(ts.length, list.length);
	}
	
	@Test
	public void testgetTestSuiteByIDKO() {
		System.out.println("\n***testgetTestSuiteByIDKO***\n");
		
		TestSuite[] list = adapter.getTestSuiteByID (null);
		Assert.assertNull(list);
	}	
	
	@Test
	public void testgetTestCase() {
		System.out.println("\n***testgetTestCase***\n");

		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);

		TestSuite[] ts = adapter.getFirstLevelTestSuitesForTestProject(result[0].getId());
		Assert.assertNotNull(ts);
		
		TestCase[] tc = adapter.getTestCasesForTestSuite(ts[0].getId(), true, TestCaseDetails.FULL);
		Assert.assertNotNull(tc);
		System.out.println("id " + tc[0].getId());
		System.out.println("id internal " + tc[0].getInternalId());
		System.out.println("id external " + tc[0].getFullExternalId());
		System.out.println("version " + tc[0].getVersion());
		System.out.println("version id " + tc[0].getVersionId());
		System.out.println("name " + tc[0].getName());
		
		String externalId = String.copyValueOf(tc[0].getFullExternalId().toCharArray(), 1 + result[0].getPrefix().length(), tc[0].getFullExternalId().length()-result[0].getPrefix().length()-1);
		
		TestCase res = adapter.getTestCase(tc[0].getId(), Integer.parseInt(externalId), tc[0].getVersion());
		Assert.assertNotNull(res);

		System.out.println("id " + res.getId());
		System.out.println("id internal " + res.getInternalId());
		System.out.println("id external " + res.getFullExternalId());
		System.out.println("version " + res.getVersion());
		System.out.println("version id " + res.getVersionId());
		System.out.println("name " + res.getName());
	}

	@Test
	public void testgetTestCaseKO() {
		System.out.println("\n***testgetTestCaseKO***\n");
		
		TestCase res = adapter.getTestCase(0, 0, 0);
		Assert.assertNull(res);
	}
	
	@Test
	public void testgetTestCasesForTestSuiteKO() {
		System.out.println("\n***testgetTestCasesForTestSuiteKO***\n");

		TestCase[] list = adapter.getTestCasesForTestSuite(0, false, TestCaseDetails.SIMPLE);
		Assert.assertNull(list);
	}
	
	@Test
	public void testgetTestCasesForTestPlanKO() {
		System.out.println("\n***getTestCasesForTestPlanKO***\n");

		TestCase[] list = adapter.getTestCasesForTestPlan(0, null, null, null, null, null, null, null, ExecutionType.MANUAL, false, null);
		Assert.assertNull(list);
	}
    
	@Test
	public void testgetTestCasesIDByName() {
		System.out.println("\n***testgetTestCasesIDByName***\n");

		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);

		TestPlan[] obj = adapter.getProjectTestPlans(result[0].getId());
		
		TestCase[] list = adapter.getTestCasesForTestPlan(obj[0].getId(), null, null, null, null, null, null, null, ExecutionType.MANUAL, false, null);
		Assert.assertNotNull(list);
		
		Integer res = adapter.getTestCaseIDByName(list[0].getName(), null, result[0].getName(), null);
		Assert.assertNotNull(res);
		Assert.assertEquals(list[0].getId(), res);
	}

	@Test
	public void testgetTestCasesIDByNameKO() {
		System.out.println("\n***testgetTestCasesIDByNameKO***\n");

		Integer res = adapter.getTestCaseIDByName("", null, "", null);
		Assert.assertNull(res);
	}

	@Test
	public void	testgetTestSuitesForTestPlan()  {
		System.out.println("\n***testgetTestSuitesForTestPlan***\n");
		
		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);

		TestPlan[] obj = adapter.getProjectTestPlans(result[0].getId());
		
		TestSuite[] list = adapter.getTestSuitesForTestPlan(obj[0].getId());
		Assert.assertNotNull(list);
	}
	
	@Test
	public void	testgetTestSuitesForTestPlanKO()  {
		System.out.println("\n***testgetTestSuitesForTestPlanKO***\n");
	    		
		TestSuite[] list = adapter.getTestSuitesForTestPlan(0);
		Assert.assertNull(list);
	}

    @Test
	public void	testgetTestSuitesForTestSuite()  {
		System.out.println("\n***testgetTestSuitesForTestSuite***\n");
		
		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);

		TestSuite[] list = adapter.getFirstLevelTestSuitesForTestProject(result[0].getId());
		Assert.assertNotNull(list);
    	
		TestSuite[] ts = adapter.getTestSuitesForTestSuite(list[0].getId());
		Assert.assertNotNull(ts);
	}

    @Test
	public void	testgetTestSuitesForTestSuiteKO()  {
		System.out.println("\n***testgetTestSuitesForTestSuiteKO***\n");
	    		
		TestSuite[] list = adapter.getTestSuitesForTestSuite(0);
		Assert.assertNull(list);
	}
	
    
    @Test
	public void	testgetFirstLevelTestSuitesForTestProjectKO()  {
		System.out.println("\n***testgetFirstLevelTestSuitesForTestProjectKO***\n");
	    		
		TestSuite[] list = adapter.getFirstLevelTestSuitesForTestProject(0);
		Assert.assertNull(list);
	}
    
    @Test
   	public void testGetLastExecutionResults() {
   		System.out.println("\n***testgetLastExecutionResult***\n");

   		TestProject result = adapter.getTestProjectByName("UQASARexample");
   		Assert.assertNotNull(result);
   		
   		TestPlan[] tp = adapter.getProjectTestPlans(result.getId());
   		Assert.assertNotNull(tp);
   		
   		TestSuite[] ts = adapter.getFirstLevelTestSuitesForTestProject(result.getId());
   		Assert.assertNotNull(ts);

   		TestCase[] tc = adapter.getTestCasesForTestSuite(ts[0].getId(), true, TestCaseDetails.FULL);
   		Assert.assertNotNull(tc);
   		System.out.println("id " + tc[0].getId());
   		System.out.println("id internal " + tc[0].getInternalId());
   		System.out.println("id external " + tc[0].getFullExternalId());
   		System.out.println("version " + tc[0].getVersion());
   		System.out.println("version id " + tc[0].getVersionId());
   		System.out.println("name " + tc[0].getName());
   		
   		String externalId = String.copyValueOf(tc[0].getFullExternalId().toCharArray(), 1 + result.getPrefix().length(), tc[0].getFullExternalId().length()-result.getPrefix().length()-1);
   		System.out.println("externalId " + externalId);
   		Execution ex = adapter.getLastExecutionResult(tp[0].getId(), tc[0].getId(), Integer.parseInt(externalId));
   		Assert.assertNotNull(ex);
    }
   
    @Test
	public void testgetLastExecutionResultKO() {
		System.out.println("\n***testgetLastExecutionResultKO***\n");
		
		Execution ex = adapter.getLastExecutionResult(0, 0, 0);
		Assert.assertNull(ex);
    }
    
    @Test
	public void testgetBuildsForTestPlanKO() {
		System.out.println("\n***testgetBuildsForTestPlanKO***\n");

		Build[] list = adapter.getBuildsForTestPlan(0);
		Assert.assertNull(list);
	}
    
    @Test
	public void testgetLatestBuildForTestPlan() {
		System.out.println("\n***testgetLatestBuildForTestPlan***\n");

		TestProject[] result = adapter.getProjects();
		Assert.assertNotNull(result);
		
		TestPlan[] tp = adapter.getProjectTestPlans(result[0].getId());
		Assert.assertNotNull(tp);
		
		Build res = adapter.getLatestBuildForTestPlan(tp[0].getId());
		Assert.assertNotNull(res);
    }
    
    @Test
	public void testgetLatestBuildForTestPlanKO() {
		System.out.println("\n***testgetLatestBuildForTestPlanKO***\n");

		Build res = adapter.getLatestBuildForTestPlan(0);
		Assert.assertNull(res);
	}
    
    @Test
	public void testgetTotalsForTestPlan() {
		System.out.println("\n***testgetTotalsForTestPlan***\n");

		//Project without platforms
		TestProject tpj = adapter.getTestProjectByName("Securitas Direct - Web");
		TestPlan[] tp = adapter.getProjectTestPlans(tpj.getId());
		Assert.assertNotNull(tp);
		System.out.println("TestProject: "+tpj.getName());
		System.out.println("TestPlan: "+tp[0].getName());

		Map<String, Object> map = adapter.getTotalsForTestPlan(tp[0].getId());
		Assert.assertNotNull(map);
		
		//Si existen plataformas asignadas error
		Object[] withTester = (Object[]) map.get("with_tester");
		HashMap<String, Object> platf = (HashMap<String, Object>) withTester[0];
		
		String passed = String.valueOf(((HashMap<String, Integer>)platf.get("p")).get("exec_qty"));
		System.out.println("passed : "+passed);
		String failed = String.valueOf(((HashMap<String, Integer>)platf.get("f")).get("exec_qty"));
		System.out.println("failed : "+failed);
		String blocked = String.valueOf(((HashMap<String, Integer>)platf.get("b")).get("exec_qty"));
		System.out.println("blocked : "+blocked);
		String notrun = String.valueOf(((HashMap<String, Integer>)platf.get("n")).get("exec_qty"));
		System.out.println("notrun : "+notrun);

		
		Object[] total = (Object[]) map.get("total");
		System.out.println("total : "+ ((HashMap<String, Integer>) total[0]).get("qty"));
		
		//Project with platforms
		TestProject tpjplat = adapter.getTestProjectByName("UQASARexample");
		TestPlan[] tpp = adapter.getProjectTestPlans(tpjplat.getId());
		Assert.assertNotNull(tpp);
		System.out.println("TestProject: "+tpjplat.getName());
		System.out.println("TestPlan: "+tpp[0].getName());

		Map<String, Object> mapp = adapter.getTotalsForTestPlan(tp[0].getId());
		Assert.assertNotNull(mapp);
    }

    @Test
	public void testgetTotalsForTestPlanKO() {
		System.out.println("\n***testgetTotalsForTestPlanKO***\n");

		Map<String, Object> map = adapter.getTotalsForTestPlan(0);
		Assert.assertNull(map);
	}


    @Test
	public void testgetMeasurementsWithPlatforms() {
		System.out.println("\n***testgetMeasurementsWithPlatforms***\n");

		Map<String, String> params = new HashMap<String, String>();
	
		params.put("testProjectName", "UQASARexample");
		params.put("testPlanName", "Prototype");
	
		try {
			List<Measurement> passed = adapter.getMeasurement("TEST_P", params);
			Assert.assertNotNull(passed);
			for (Measurement meas : passed){
				System.out.println("Passed: "+meas.getMeasurement());
			}

			List<Measurement> failed = adapter.getMeasurement("TEST_F", params);
			Assert.assertNotNull(failed);
			for (Measurement meas : failed){
				System.out.println("Failed: "+meas.getMeasurement());
			}

			List<Measurement> blocked = adapter.getMeasurement("TEST_B", params);
			Assert.assertNotNull(blocked);
			for (Measurement meas : blocked){
				System.out.println("Blocked: "+meas.getMeasurement());
			}
			
			List<Measurement> notrun = adapter.getMeasurement("TEST_N", params);
			Assert.assertNotNull(notrun);
			for (Measurement meas : notrun){
				System.out.println("Not run: "+meas.getMeasurement());
			}
			
			List<Measurement> total = adapter.getMeasurement("TEST_TOTAL", params);
			Assert.assertNotNull(total);
			for (Measurement meas : total){
				System.out.println("Total: "+meas.getMeasurement());
			}
		
		} catch (uQasarException e) {
			System.out.println("\n***testgetMeasurementsWithPlatforms***\n"+e.getMessage());
		}
	}

    @Test
	public void testgetMeasurementsWithoutPlatforms() {
		System.out.println("\n***testgetMeasurementsWithoutPlatforms***\n");

		Map<String, String> params = new HashMap<String, String>();
		params.put("testProjectName", "Securitas Direct - Web");
		params.put("testPlanName", "Securitas Direct - Web: Ciclo 2");
	
		try {
			List<Measurement> passed = adapter.getMeasurement("TEST_P", params);
			Assert.assertNotNull(passed);
			for (Measurement meas : passed){
				System.out.println("Passed: "+meas.getMeasurement());
			}

			List<Measurement> failed = adapter.getMeasurement("TEST_F", params);
			Assert.assertNotNull(failed);
			for (Measurement meas : failed){
				System.out.println("Failed: "+meas.getMeasurement());
			}

			List<Measurement> blocked = adapter.getMeasurement("TEST_B", params);
			Assert.assertNotNull(blocked);
			for (Measurement meas : blocked){
				System.out.println("Blocked: "+meas.getMeasurement());
			}
			
			List<Measurement> notrun = adapter.getMeasurement("TEST_N", params);
			Assert.assertNotNull(notrun);
			for (Measurement meas : notrun){
				System.out.println("Not run: "+meas.getMeasurement());
			}
			
			List<Measurement> total = adapter.getMeasurement("TEST_TOTAL", params);
			Assert.assertNotNull(total);
			for (Measurement meas : total){
				System.out.println("Total: "+meas.getMeasurement());
			}
		
			
		} catch (uQasarException e) {
			System.out.println("\n***testgetMeasurementsWithoutPlatforms***\n"+e.getMessage());
		}
	}

    @Test
	public void testGetBugsByTestPlan(){
		System.out.println("\n***testGetBugsByTestPlan***\n");

			Map<String, String> params = new HashMap<String, String>();
			params.put("testProjectName", "UQASARexample");
			params.put("testPlanName", "TestPlanBBVA");
			params.put("serverDB", "jdbc:mysql://maquetas.mtp.es:3306/testlink");
			params.put("userDB", "dev");
			params.put("passwordDB", "Mtp2011");
			
			
			try {
				List<Measurement> bugs = adapter.getMeasurement("BUGS_PLAN", params);
				Assert.assertNotNull(bugs);
			} catch (uQasarException ex) {
				System.out.println("\n***testGetBugsByTestPlan***\n"+ex.getMessage());
			}
			
	}
    
    
//    @Test
//	public void testDBConnection() throws SQLException{
//		System.out.println("\n***testDBConnection***\n");
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("testProjectName", "UQASARexample");
//			params.put("testPlanName", "TestPlanBBVA");
//			String serverDB = "jdbc:mysql://maquetas.mtp.es:3306/testlink";
//			String userDB = "dev";
//			String passwordDB = "Mtp2011";
//			
//			Statement stmt = null;
//			ResultSet rs = null;
//			Connection connection = null;
//
//			try {
//				System.out.println("sever DB" + serverDB);
//
//				Class.forName("com.mysql.jdbc.Driver");				
//				//Error 
//				//java.sql.SQLException: null,  message from server: "Host '213.172.45.2' is blocked because of many connection errors; unblock with 'mysqladmin flush-hosts'"
//				connection = DriverManager.getConnection(serverDB, userDB, passwordDB);
//				stmt = connection.createStatement();
//				
//				String query = "SELECT * FROM execution_bugs, executions, builds WHERE execution_bugs.execution_id = executions.id AND executions.build_id = builds.id AND builds.testplan_id = 65 ORDER BY builds.name,bug_id";
//				System.out.println("query :" + query);
//				rs = stmt.executeQuery(query);
//				
//				while (rs.next()) {
//				   System.out.println("nombre="+rs.getObject("bug_id"));
//				}
//				
//			} catch (ClassNotFoundException ex) {
//				ex.printStackTrace();
//			} catch (SQLException ex) {
//				ex.printStackTrace();
//			} finally {
//				if (rs != null) {
//					rs.close();
//				}
//				if (stmt !=null) {
//					stmt.close();
//				}
//				if (connection!=null){
//					connection.close();
//				}
//			}
//	}    
  
}
