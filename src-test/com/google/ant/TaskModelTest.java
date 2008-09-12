package com.google.ant;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

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

    model.addClasspath(new Path(new Project()));
    model.setResultFile("/this/result/file/does/not/exist.txt");
    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_RESULT_FILE_CREATION_FAILED));
  }

  public void testErrorStreamError() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addClasspath(new Path(new Project()));
    model.setErrorFile("/this/error/file/does/not/exist.txt");
    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_ERROR_FILE_CREATION_FAILED));
  }

  public void testGetClassPath() throws Exception {
    Project proj = new Project();
    Path p = new Path(proj);

    proj.setBasedir(".");
    p.setLocation(new File("src-test/com/google/ant"));

    model.addClasspath(p);
    Matcher matcher = Pattern.compile("src-test.com.google.ant").matcher(model.getClassPath());
    assertTrue(matcher.find());
  }

  public void testResultFileNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addClasspath(new Path(new Project()));
    model.setResultFile(null);
    assertTrue(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_RESULT_FILE_NOT_SET));
  }

  public void testFilterNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addClasspath(new Path(new Project()));
    model.setFilter(null);
    assertTrue(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_FILTER_NOT_SET));
  }

  public void testErrorFileSameAsResultFile() throws Exception {
    List<String> messages = new ArrayList<String>();

    model.addClasspath(new Path(new Project()));
    model.setErrorFile(null);
    model.setResultFile(File.createTempFile("anyfile", ".temp").toString());
    assertTrue(model.validate(messages));
    assertEquals(model.getResultFile(), model.getErrorFile());
  }

  public void testFileSetNotSet() throws Exception {
    List<String> messages = new ArrayList<String>();

    assertFalse(model.validate(messages));
    assertTrue(messages.contains(TaskModel.ERROR_FILESET_NOT_SET));
  }
}

