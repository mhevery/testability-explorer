package com.google.ant;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;

public class TestabilityTaskTest extends TestCase
{
    private TestabilityTask task;
    private Project project;

    public TestabilityTaskTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp()
    {
        task = new TestabilityTask();
        project = new Project();

        task.setProject(project);
    }

    @Override
    protected void tearDown()
    {
    }

    public void testNothing() throws Exception
    {
        assertTrue(true);
    }


//  public void testInnerClassReference()
//  {
//    try
//    {
//      FileSet fileset = new FileSet();
//
//      project.setBasedir(".");
//
//      fileset.setDir(project.resolveFile("../java/test/"));
//      fileset.setIncludes("**/InnerClassReference.xava");
//      task.addFileSet(fileset);
//      task.setProject(project);
//      task.setResultsformatter(
//              "rj.tools.jcsc.formatter.XMLResultsFormatter");
//      task.setRules("../deploy/rules/jcsc.jcsc.xml");//rules/test.default.jcsc.xml");
//      task.setDestdir("../../tmp");
//
//      task.setJcscHome("..");
//      task.setCopyAssociatedFiles("true");
//      task.execute();
//
//      HashMap pm = task.getPackageMap();
//
//      assertEquals(1, pm.size());
//
//      int classCount = 0;
//      int methodCount = 0;
//      int violationCount = 0;
//      int ncssCount = 0;
//      int unittestclassCount = 0;
//      int unittestsCount = 0;
//
//      for (Iterator iter = pm.keySet().iterator(); iter.hasNext();)
//      {
//        PackageStatistics ps = (PackageStatistics) pm.get(iter.next());
//
//        //System.out.println(ps.toString());
//
//        classCount += ps.getClassCount();
//        methodCount += ps.getMethodCount();
//        violationCount += ps.getViolationCount();
//        ncssCount += ps.getNCSSCount();
//        unittestclassCount += ps.getUnitTestClassCount();
//        unittestsCount += ps.getUnitTestCount();
//      }
//
//      assertEquals(1, classCount);
//      assertEquals(6, methodCount);
//      assertEquals(4, violationCount);
//      assertEquals(16, ncssCount);
//      assertEquals(0, unittestclassCount);
//      assertEquals(0, unittestsCount);
//
//      //System.out.println("Classes    :" + classCount);
//      //System.out.println("Methods    :" + methodCount);
//      //System.out.println("Violations :" + violationCount);
//      //System.out.println("NCSS       :" + ncssCount);
//      //System.out.println("unitCC     :" + unittestclassCount);
//      //System.out.println("unittests  :" + unittestsCount);
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace(System.err);
//
//      fail("An exception should NOT have been thrown");
//    }
//  }
}
