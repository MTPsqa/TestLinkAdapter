package eu.uqasar.testlink.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.Execution;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.uQasarMetric;


/**
 * TestLinkAdapter.
 * Class to connect the TestLink tool using TestLinkAPI.
 * @author mtp233
 *
 */
public class TestLinkAdapter {
	
	// Substitute your Dev Key Here
	private String key;
    // Substitute your Server URL Here
	private String server_url;
    
    private TestLinkAPI api;
	
    
    private static final Map<String, String> UQMETRIC_TO_TLMETRIC = new TreeMap<String, String>() {
        {
          put("TEST_P", "TEST_P");
          put("TEST_F", "TEST_F");
          put("TEST_B", "TEST_B");
          put("TEST_N", "TEST_N");
          put("TEST_TOTAL", "TEST_TOTAL");
        }
      };
      
      
      private static final Map<String, uQasarMetric> UQM_NAME_TO_VALUE = new TreeMap<String, uQasarMetric>() {
    	  {
    		  for (uQasarMetric uqm : uQasarMetric.values()) {
    			  put(uqm.name(), uqm);
    		  }
    	  }
      };
    
      
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] {
          new X509TrustManager() {
              public X509Certificate[] getAcceptedIssuers() {
                  return null;
              }
   
              public void checkClientTrusted(X509Certificate[] certs, String authType) {
                  // Trust always
              }
   
              public void checkServerTrusted(X509Certificate[] certs, String authType) {
                  // Trust always
              }
          }
      };
    
    /**
     * Initalize class.
     * @param url testlink server url
     * @param key developer key
     * @throws uQasarException 
     */
    public TestLinkAdapter(String url, String dev_key) throws uQasarException {
    	key = dev_key;
    	server_url = url;
    	init();
    }
    
    private void init () throws uQasarException {
    	// Install the all-trusting trust manager
    	SSLContext sc;
    	try {
    		sc = SSLContext.getInstance("SSL");

    		// Create empty HostnameVerifier
    		HostnameVerifier hv = new HostnameVerifier() {
    			public boolean verify(String arg0, SSLSession arg1) {
    				return true;
    			}
    		};

    		sc.init(null, trustAllCerts, new java.security.SecureRandom());
    		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    		HttpsURLConnection.setDefaultHostnameVerifier(hv);
    		api = new TestLinkAPI(new URL(server_url), key);
    	} catch (MalformedURLException ex) {
    		throw new uQasarException(ex.getMessage());
    	} catch (NoSuchAlgorithmException ex) {
    		throw new uQasarException(ex.getMessage());
    	} catch (KeyManagementException ex) {
    		throw new uQasarException(ex.getMessage());
    	} catch (TestLinkAPIException ex) {
    		throw new uQasarException(ex.getMessage());
    	}
    }
    
    /**
     * getProjects.
     * @return list of all TestProject in TestLink installation.
     */
    protected TestProject[] getProjects() {
    	TestProject[] list;

    	try {
        	list = api.getProjects();
    	} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	return list;
    }
    
    
    /**
     * getTestProjectByName: Get a TestProject by its name.
     * @param projectName Name of the TestProject to get.
     * @return TestProject found with projectName.
     */
    protected TestProject getTestProjectByName (String projectName) {
    	TestProject res = null;
    	
    	try {
        	res = api.getTestProjectByName(projectName);
    	} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}
		return res;
    }

    /**
     * getTestPlanByName: Get a TestPlan by its name.
     * @param planName 
     * @param projectName Name of the TestProject where the TestPlan belongs to.
     * @return TestPlan found with planName.
     */    
    protected TestPlan getTestPlanByName (String planName, String projectName) {
    	TestPlan res = null;
		
    	try {
        	res = api.getTestPlanByName(planName, projectName);
    	} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}
    	
		return res;
    }
    
    		
    /**
     * getProjectTestPlans: Get a list of all the existing test plans for a project by id.
     * @param projectId
     * @return list of TestPlan belonging to the provided TestProject id.
     */
    protected TestPlan[] getProjectTestPlans (Integer projectId) {
    	TestPlan[] list = null;

    	try {
        	list = api.getProjectTestPlans(projectId);
    	} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	
    	return list;
    }

    /**
     * getTestPlanPlatforms: Get a list of all platforms in a TestPlan. 
     * @param planId
     * @return list of Platform belonging to the provided TestPlan id.
     */
    private Platform[] getTestPlanPlatforms (Integer planId) {
    	Platform[] list = null;
    	
    	try {
    		list = api.getTestPlanPlatforms(planId);
    	} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	return list;
    }

    /**
     * getTestSuiteByID: Get a list of TestSuites.
     * @param testSuiteIds ids of the TestSuites to get.
     * @return list of TestSuite.
     */
    protected TestSuite[] getTestSuiteByID (List<Integer> testSuiteIds) {
    	TestSuite[] list = null;
    	
    	try {
        	list = api.getTestSuiteByID(testSuiteIds);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	
		return list;
    }

    /**
     * getTestCase: Get the details of a given TestCase.
     * @param testCaseId
     * @param testCaseExternalId external id of the TestCase to get.
     * @param version version of the TestCase to get.
     * @return
     */
    protected TestCase getTestCase (Integer testCaseId, Integer testCaseExternalId, Integer version) {
		TestCase res = null;
    	
    	try {
    		//it seems that version field has not influence?
    		res = api.getTestCase(testCaseId, testCaseExternalId, version);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}
    	return res;
    }

    /**
     * getTestCasesForTestSuite: Get TestCases belonging to a TestSuite.
     * @param testSuiteId
     * @param deep Set the deep flag to false if you only want test cases in the test suite provided and no child test cases.
     * @param detail optional (default is simple) use full if you want to get summary,steps & expected_results.
     * @return list of TestCase belonging to the TestSuite.
     */
    protected TestCase[] getTestCasesForTestSuite (Integer testSuiteId, boolean deep, TestCaseDetails detail) {
		TestCase[] list = null;
		
		try {
			list = api.getTestCasesForTestSuite(testSuiteId, deep, detail);
	    } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
		
    	return list;
    }

    /**
     * getTestCasesForTestPlan: Get TestCases belonging to a TestPlan.
     * @param testPlanId TestPlan id.
     * @param testCasesIds 
     * @param buildId
     * @param keywordsIds
     * @param keywords
     * @param executed
     * @param assignedTo
     * @param executeStatus
     * @param executionType
     * @param getStepInfo
     * @param detail
     * @return list of TestCase.
     */
    protected TestCase[] getTestCasesForTestPlan (Integer testPlanId, List<Integer> testCasesIds, Integer buildId, List<Integer> keywordsIds, String keywords, Boolean executed, List<Integer> assignedTo, String[] executeStatus, ExecutionType executionType, Boolean getStepInfo, TestCaseDetails detail) {
    	TestCase[] list = null;
    	
		try {
	    	list = api.getTestCasesForTestPlan(testPlanId, testCasesIds, buildId, keywordsIds, keywords, executed, assignedTo, executeStatus, executionType, getStepInfo, detail);
	    } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	
    	return list;
    }

    /**
     * getTestCaseIDByName get TestCase id by providing its name.
     * @param testCaseName
     * @param testSuiteName
     * @param testProjectName
     * @param testCasePathName
     * @return TestCase id.
     */
    protected Integer getTestCaseIDByName (String testCaseName, String testSuiteName, String testProjectName, String testCasePathName) {
    	Integer res = null;
    	
		try {
	    	res = api.getTestCaseIDByName(testCaseName, testSuiteName, testProjectName, testCasePathName);
	    } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}

    	return res;
    }

    /**
     * getTestSuitesForTestPlan Get a list of TestSuite for a given TestPlan id.
     * @param testPlanId 
     * @return list of TestSuite belonging to the TestPlan.
     */
    protected TestSuite[] getTestSuitesForTestPlan(Integer testPlanId)  {
    	TestSuite[]	list = null;
    	
    	try {
    		list = api.getTestSuitesForTestPlan(testPlanId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	
		return list;
    }
   
    /**
     * getTestSuitesForTestSuite Get list of TestSuite for a given TestSuit id.
     * @param testSuiteId
     * @return list of TestSuit belonging to the TestSuite.
     */
    protected TestSuite[] getTestSuitesForTestSuite(Integer testSuiteId) {
    	TestSuite[] list = null;
    	
    	try {
    		list = api.getTestSuitesForTestSuite(testSuiteId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
		return list;
    }
   
    /**
     * getFirstLevelTestSuitesForTestProject get firstlevel of TestSuites for given TestProject id.
     * @param testProjectId
     * @return list of TestSuite of first level belonging to the TestProject.
     */
    protected TestSuite[] getFirstLevelTestSuitesForTestProject(Integer testProjectId) {
		TestSuite[] list = null;
		
		try {
			list = api.getFirstLevelTestSuitesForTestProject(testProjectId);
		} catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
    	return list;
    }

    /**
     * getLastExecutionResult get the result of the last execution of a TestCase.
     * @param testPlanId
     * @param testCaseId
     * @param testCaseExternalId
     * @return Execution details.
     */
    protected Execution getLastExecutionResult(Integer testPlanId, Integer testCaseId, Integer testCaseExternalId) {
    	Execution res = null;
    	
    	try {
    		res = api.getLastExecutionResult(testPlanId, testCaseId, testCaseExternalId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}
    	
    	return res;
    }
    
    /**
     * getBuildsForTestPlan get list of Builds for a given TestPlan id.
     * @param testPlanId
     * @return list of Build linked to the TestPlan id.
     */
    protected Build[] getBuildsForTestPlan(Integer testPlanId) {
    	Build[] list = null;
    	
    	try {
    		list = api.getBuildsForTestPlan(testPlanId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		list = null;
    	}
		return list;
    }
    
    /**
     * getLatestBuildForTestPlan get the latest build for a given TestPlan id.
     * @param testPlanId
     * @return Build details.
     */
    protected Build getLatestBuildForTestPlan(Integer testPlanId) {
    	Build res = null;
    	
    	try {
    		res = api.getLatestBuildForTestPlan(testPlanId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		res = null;
    	}
    	return res;
    }
    
    
    /**
     * getTotalsForTestPlan get stats for a given TestPlan id
     * @param testPlanId
     * @return Map. Passed (p), failed(f), blocked(b), notrun (n) tests with tester assigned ("with_tester"). 
     */
    protected Map<String,Object> getTotalsForTestPlan(Integer testPlanId) {
		
		Map<String,Object>	map = null;
		
    	try {
    		map= api.getTotalsForTestPlan(testPlanId);
        } catch (TestLinkAPIException ex) {
    		System.out.println("TestLinkAPIException " + ex.getMessage());
    		map = null;
    	}
		return map;
	}    

    private String mapMetricName(String metric) throws uQasarException {
        String testLinkMetricName = UQMETRIC_TO_TLMETRIC.get(metric);
        if (testLinkMetricName == null) {
          throw new uQasarException(String.format("The UASAR metric %s is unknown in TestLink", metric));
        }
        return testLinkMetricName;
      }
    
    
    /**
     * Method to obtain the metrics from TestLink tool.
     * @param metric String containing comma-separated names of metrics to retrieve.
     * @param params Map<String,String> with values to obtain the metric.
     * Keys:
     * testProjectName
     * testPlanName
     * serverDB. example: jdbc:mysql://maquetas.mtp.es:3306/testlink
     * userDB. user: dev
     * passwordDB. password: Mtp2011
     * @return List<Measurement> List of measurements.
     * @throws uQasarException Exception
     */
    public List<Measurement> getMeasurement(String metric, Map<String, String> params) throws uQasarException {
      LinkedList<Measurement> measurements = new LinkedList<Measurement>();

      List<String> metricsToQuery = Arrays.asList(metric.split(","));
      Measurement meas = null;
      String testProjectName = params.get("testProjectName");
      String testPlanName = params.get("testPlanName");
      String serverDB = params.get("serverDB");
      String userDB = params.get("userDB");
      String passwordDB = params.get("passwordDB");
      
      String val;

      try  {
    	  for (String met : metricsToQuery) {
    		  TLMetric response = TLMetric.valueOf(met);
        
    		  switch (response){
    		  case TEST_P: 
        		val = this.getNumberTestsPlanStatus(testProjectName, testPlanName, "p");
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("TEST_P"), val);
        	    break;
    		  case TEST_F: 
        		val = this.getNumberTestsPlanStatus(testProjectName, testPlanName, "f");
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("TEST_F"), val);
        	    break;
        	case TEST_B: 
        		val = this.getNumberTestsPlanStatus(testProjectName, testPlanName, "b");
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("TEST_B"), val);
        	    break;
        	case TEST_N: 
        		val = this.getNumberTestsPlanStatus(testProjectName, testPlanName, "n");
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("TEST_N"), val);
        		break;
        	case TEST_TOTAL: 
        		val = this.getNumberTestPlanTotals(testProjectName, testPlanName);
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("TEST_TOTAL"), val);
        		break;
        	case BUGS_PLAN: 
        		val = this.getNumberBugsTestPlan(testProjectName, testPlanName, serverDB, userDB, passwordDB);
        		meas = new Measurement(UQM_NAME_TO_VALUE.get("BUGS_PLAN"), val);
        		break;
        		
        		
        	default:
        		meas=null;
        } 
        measurements.add(meas);
      }
    	  
      } catch (SQLException ex){
    	  throw new uQasarException(ex.getMessage());
      }

      return measurements;
    }

    
    /**
     * Obtain number of tests last build by result
     * @param projectName project name in TestLink
     * @param planName plan name in TestLink
     * @param res result p: passed, f: failed, b: blocked, n:notrun
     * @return number of tests which match the result
     */
    protected String getNumberTestsPlanStatus(String projectName, String planName, String res) {
  		String qty = "0";
  		
  		TestProject tpj = api.getTestProjectByName(projectName);
  		TestPlan tp = api.getTestPlanByName(planName, projectName);
  		
		Map<String, Object> map = api.getTotalsForTestPlan(tp.getId());
  			
		Platform[] plats = this.getTestPlanPlatforms(tp.getId());
			
		
  		if (plats == null || plats.length == 0){
  			Object[] withTester = (Object[]) map.get("with_tester");
  			HashMap<String, Object> platf = (HashMap<String, Object>) withTester[0];
		
  			qty = String.valueOf(((HashMap<String, Integer>)platf.get(res)).get("exec_qty"));

  		} else {
  			Integer total = 0;
  			for (Platform pl : plats){
  				
  				HashMap<String,Object> maptotal = (HashMap<String, Object>) ((HashMap<String, Object>)map.get("with_tester")).get(String.valueOf(pl.getId()));
  				
  				HashMap<String,String> mappartial = (HashMap<String, String>) (maptotal).get(res);
  				
  				total = total + Integer.valueOf(String.valueOf(mappartial.get("exec_qty")));
  			}
  			qty = String.valueOf(total);
  		}

  		return qty.toString();
    }

    
    /**
     * Obtain total number of tests from last build.
     * @param projectName
     * @param planName
     * @return number of tests.
     */
    protected String getNumberTestPlanTotals(String projectName, String planName) {
  		String qty;
  	
  		TestPlan tp = api.getTestPlanByName(planName, projectName);
  		Map<String, Object> map = api.getTotalsForTestPlan(tp.getId());
		
		Platform[] plats = this.getTestPlanPlatforms(tp.getId());
  		
  		if (plats == null || plats.length == 0){
  			Object[] total = (Object[]) map.get("total");
  			qty = String.valueOf(((HashMap<String, Integer>) total[0]).get("qty"));
  		} else {
  			Integer total = 0;
  			for (Platform pl : plats){
  				//Object maptotal = ((HashMap<String, Object>)map.get("total")).get(pl.getId());
  				HashMap<String,Object> maptotal = (HashMap<String, Object>) ((HashMap<String, Object>)map.get("total")).get(String.valueOf(pl.getId()));
  				
  				total = total + Integer.valueOf(String.valueOf(((Map<String, Object>) maptotal).get("qty")));
  			}
  			qty = String.valueOf(total);
  		}
  		return qty;
    }

    
    /**
     * Obtain total number of bugs for TestPlan.
     * @param projectName
     * @param planName
     * @param serverDB
     * @param userDB
     * @param passwordDB
     * @return number of bugs
     * @throws SQLException 
     */
    protected String getNumberBugsTestPlan(String projectName, String planName, String serverDB, String userDB, String passwordDB) throws uQasarException, SQLException {
  		String qty= "0";
  	
  		TestPlan tp = api.getTestPlanByName(planName, projectName);
  		
  		Statement stmt = null;
		ResultSet rs = null;
		Connection connection = null;

		try {
			System.out.println("sever DB" + serverDB);

			Class.forName("com.mysql.jdbc.Driver");
			
			//Error java.sql.SQLException: null,  message from server: "Host '213.172.45.2' is blocked because of many connection errors; unblock with 'mysqladmin flush-hosts'"
			connection = DriverManager.getConnection(serverDB, userDB, passwordDB);
			stmt = connection.createStatement();
			
			String query = "SELECT * FROM execution_bugs, executions, builds WHERE execution_bugs.execution_id = executions.id AND executions.build_id = builds.id AND builds.testplan_id = "+tp.getId()+" ORDER BY builds.name,bug_id";
			System.out.println("query :" + query);
			
			
			rs = stmt.executeQuery(query);
			
			while (rs.next()) {
			   System.out.println("nombre="+rs.getObject("bug_id"));
			}
			
			
//			String queryCount = "SELECT COUNT(*) FROM execution_bugs, executions, builds WHERE execution_bugs.execution_id = executions.id AND executions.build_id = builds.id AND builds.testplan_id = "+tp.getId()+" ORDER BY builds.name,bug_id";
			String queryCount = "SELECT COUNT(*) FROM execution_bugs, executions, builds WHERE execution_bugs.execution_id = executions.id AND executions.build_id = builds.id AND builds.testplan_id = 0 ORDER BY builds.name,bug_id";
			
			System.out.println("query :" + queryCount);
			rs = stmt.executeQuery(queryCount);
			
			while (rs.next()) {
				qty = rs.getString(1);
				System.out.println("total bugs ="+qty);
			}
			
			
		} catch (ClassNotFoundException ex) {
			throw new uQasarException(ex.getMessage());
		} catch (SQLException ex) {
			throw new uQasarException(ex.getMessage());
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt !=null) {
				stmt.close();
			}
			if (connection!=null){
				connection.close();
			}
		}
  		return qty;
    }
    
    
}
