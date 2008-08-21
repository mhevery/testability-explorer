package com.google.ant;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class TaskModelTest extends TestCase {
  private TaskModel model;

  public TaskModelTest(String name)
  {
    super(name);
  }

  @Override
  protected void setUp()
  {
    model = new TaskModel();
  }

  @Override
  protected void tearDown()
  {
  }

  public void testDefaultPrintDepth()  throws Exception {
    assertEquals(model.getPrintDepth(), -1);
    model.validate(new ArrayList<String>());
    assertEquals(model.getPrintDepth(), TaskModel.DEFAULT_PRINT_DEPTH);
  }

  public void testDefaultMinCost()  throws Exception {
    assertEquals(model.getMinCost(), -1);
    model.validate(new ArrayList<String>());
    assertEquals(model.getMinCost(), TaskModel.DEFAULT_MIN_COST);
  }

  public void testDefaultMaxExcellenceCost()  throws Exception {
    assertEquals(model.getMaxExcellentCost(), -1);
    model.validate(new ArrayList<String>());
    assertEquals(model.getMaxExcellentCost(), TaskModel.DEFAULT_MAX_EXCELLENT_COST);
  }

  public void testDefaultMaxAcceptableCost()  throws Exception {
    assertEquals(model.getMaxAcceptableCost(), -1);
    model.validate(new ArrayList<String>());
    assertEquals(model.getMaxAcceptableCost(), TaskModel.DEFAULT_MAX_ACCEPTABLE_COST);
  }

  public void testDefaultWorstOffenderCount()  throws Exception {
    assertEquals(model.getWorstOffenderCount(), -1);
    model.validate(new ArrayList<String>());
    assertEquals(model.getWorstOffenderCount(), TaskModel.DEFAULT_WORST_OFFENDER_COUNT);
  }

  public void testDefaultWhiteList()  throws Exception {
    assertEquals(model.getWhiteList(), null);
    model.validate(new ArrayList<String>());
    assertEquals(model.getWhiteList(), TaskModel.DEFAULT_WHITE_LIST);
  }

  public void testDefaultGrouping()  throws Exception {
    assertEquals(model.getGrouing(), null);
    model.validate(new ArrayList<String>());
    assertEquals(model.getGrouing(), TaskModel.DEFAULT_GROUPING);
  }

  public void testDefaultPrint()  throws Exception {
    assertEquals(model.getPrint(), null);
    model.validate(new ArrayList<String>());
    assertEquals(model.getPrint(), TaskModel.DEFAULT_PRINT);
  }

  public void testDefaultResultFile() throws Exception {
    assertEquals(null, model.getResultFile());
    model.validate(new ArrayList<String>());
    assertEquals(model.getResultFile(), TaskModel.DEFAULT_RESULT_FILE);
  }

  public void testDefaultErrorFile() throws Exception {
    assertEquals(null, model.getErrorFile());
    model.validate(new ArrayList<String>());
    assertEquals(model.getErrorFile(), TaskModel.DEFAULT_RESULT_FILE); // error becomes result if not set
  }

  public void testDefaultFilter() throws Exception {
    assertEquals(null, model.getFilter());
    model.validate(new ArrayList<String>());
    assertEquals(model.getFilter(), TaskModel.DEFAULT_FILTER);
  }

  public void testSystemOutOutputStream() throws Exception {
    OutputStream os = model.getOutputStream("System.out");

    assertEquals(os, System.out);
  }

  public void testSystemErrOutputStream() throws Exception {
    OutputStream os = model.getOutputStream("System.err");

    assertEquals(os, System.err);
  }

  public void testResultStreamError() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addFileSet(new FileSet());
    model.setResultFile("/this/result/file/does/not/exist.txt");
    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_RESULT_FILE_CREATION_FAILED));
  }

  public void testErrorStreamError() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addFileSet(new FileSet());
    model.setErrorFile("/this/error/file/does/not/exist.txt");
    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_ERROR_FILE_CREATION_FAILED));
  }

  public void testGetClassPath() throws Exception {
    FileSet fs = new FileSet();
    Project p = new Project();

    p.setBasedir(".");
    fs.setProject(p);
    fs.setDir(new File("src-test/com/google/ant"));
    fs.setIncludes("**/*.raj");

    model.addFileSet(fs);
    assertTrue(model.getClassPath().indexOf(File.pathSeparator) >= 0);
    String[] cps = model.getClassPath().split(File.pathSeparator);
    assertEquals(Arrays.toString(cps), 2, cps.length);
    assertTrue(cps[0].endsWith("blah.raj"));
    assertTrue(cps[1].endsWith("foo.raj"));
  }

  public void testResultFileNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addFileSet(new FileSet());
    model.setResultFile(null);
    assertTrue(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_RESULT_FILE_NOT_SET));
  }

  public void testFilterNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addFileSet(new FileSet());
    model.setFilter(null);
    assertTrue(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_FILTER_NOT_SET));
  }

  public void testErrorFileSameAsResultFile() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addFileSet(new FileSet());
    model.setErrorFile(null);
    model.setResultFile("anyfile");
    assertTrue(model.validate(messages));
    assertEquals(model.getResultFile(), model.getErrorFile());
  }

  public void testFileSetNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_FILESET_NOT_SET));
  }
}

