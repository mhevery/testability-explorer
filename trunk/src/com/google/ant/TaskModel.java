package com.google.ant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

public class TaskModel {

  public static final String DEFAULT_RESULT_FILE = "System.out";
  public static final String DEFAULT_ERROR_FILE = "System.err";
  public static final String DEFAULT_FILTER = "";
  public static final String DEFAULT_WHITE_LIST = "IGNORE_ME_WHITELIST";
  public static final int DEFAULT_MAX_ACCEPTABLE_COST = 100;
  public static final int DEFAULT_MAX_EXCELLENT_COST = 50;
  public static final int DEFAULT_WORST_OFFENDER_COUNT = 20;
  public static final int DEFAULT_MIN_COST = 1;
  public static final int DEFAULT_PRINT_DEPTH = 2;
  public static final String DEFAULT_GROUPING = "cost"; // cost | package
  public static final String DEFAULT_PRINT = "summary"; // summary | detail | html
  public static final int DEFAULT_CYCLOMATIC = 1;
  public static final int DEFAULT_GLOBAL = 10;

  public static final String ERROR_FILESET_NOT_SET = "fileset to jar and/or classfile directories must be set";
  public static final String ERROR_FILTER_NOT_SET = "filter must be set. default is " + DEFAULT_FILTER + " (all)";
  public static final String ERROR_WHITE_LIST_NOT_SET = "white list not set. using default" + DEFAULT_WHITE_LIST;
  public static final String ERROR_RESULT_FILE_NOT_SET = "resultfile must be set. either a filepath or System.(out|err). default is " + DEFAULT_RESULT_FILE;
  public static final String ERROR_PRINT_DEPTH_NOT_SET = "print depth not set. using default " + DEFAULT_PRINT_DEPTH;
  public static final String ERROR_MIN_COST_NOT_SET = "min cost not set. using default " + DEFAULT_MIN_COST;
  public static final String ERROR_MAX_EXCELLENT_COST_NOT_SET = "max excellent cost not set. using default " + DEFAULT_MAX_EXCELLENT_COST;
  public static final String ERROR_MAX_ACCEPTABLE_COST_NOT_SET = "max acceptable cost not set. using default " + DEFAULT_MAX_ACCEPTABLE_COST;
  public static final String ERROR_GROUPING_NOT_SET = "grouping not set. using default " + DEFAULT_GROUPING;
  public static final String ERROR_PRINT_NOT_SET = "print not set. using default " + DEFAULT_PRINT;
  public static final String ERROR_WORST_OFFENDER_COUNT_NOT_SET = "worst offender count not set. using default " + DEFAULT_WORST_OFFENDER_COUNT;
  public static final String ERROR_CYCLOMATIC_NOT_SET = "cyclomatic not set. using default " + DEFAULT_CYCLOMATIC;
  public static final String ERROR_GLOBAL_NOT_SET = "global not set. using default " + DEFAULT_GLOBAL;
  public static final String ERROR_ERROR_FILE_SET_TO_RESULT_FILE = "error file not set. using result file.";
  public static final String ERROR_RESULT_FILE_CREATION_FAILED = "resultfile could not be created";
  public static final String ERROR_ERROR_FILE_CREATION_FAILED = "errorfile could not be created";

  private final Vector<Path> classPaths = new Vector<Path>();
  private String failProperty;
  private String resultFile = null;
  private String errorFile = null;
  private String filter = null;
  private int printDepth = -1;
  private int minCost = -1;
  private int maxExcellentCost = -1;
  private int maxAcceptableCost = -1;
  private int worstOffenderCount = -1;
  private String whiteList = null;
  private String grouping = null;
  private String print = null;
  private int cyclomatic = -1;
  private int global = -1;

  public int getCyclomatic() {
    return cyclomatic;
  }

  public void setCyclomatic(int cyclomatic) {
    this.cyclomatic = cyclomatic;
  }

  public void setMaxAcceptableCost(int cost) {
    maxAcceptableCost = cost;
  }

  public int getMaxAcceptableCost() {
    return maxAcceptableCost;
  }

  public void setMaxExcellentCost(int cost) {
    maxExcellentCost = cost;
  }

  public int getMaxExcellentCost() {
    return maxExcellentCost;
  }

  public void setMinCost(int cost) {
    minCost = cost;
  }

  public int getMinCost() {
    return minCost;
  }

  public void setGrouping(String groupingVal) {
    grouping = groupingVal;
  }

  public String getGrouing() {
    return grouping;
  }
  public void setPrint(String printVal) {
    print = printVal;
  }

  public String getPrint() {
    return print;
  }

  public void setPrintDepth(int depth) {
    printDepth = depth;
  }

  public int getPrintDepth() {
    return printDepth;
  }

  public void setWhiteList(String whiteListVal) {
    whiteList = whiteListVal;
  }

  public String getWhiteList() {
    return whiteList;
  }

  public void setWorstOffenderCount(int count) {
    worstOffenderCount = count;
  }

  public int getWorstOffenderCount() {
    return worstOffenderCount;
  }

  public void setResultFile(String resultFile) {
    this.resultFile = resultFile;
  }

  public String getResultFile() {
    return resultFile;
  }

  public void setErrorFile(String resultFile) {
    errorFile = resultFile;
  }

  public String getErrorFile() {
    return errorFile;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public int getGlobal() {
    return global;
  }

  public void setGlobal(int glob) {
    global = glob;
  }

  public String getFilter() {
    return filter;
  }

  public void setFailProperty(String property) {
    failProperty = property;
  }

  public boolean isFailPropertySet() {
    return failProperty != null && !failProperty.equals("");
  }

  public String getFailProperty() {
    return failProperty;
  }

  public void addClasspath(Path path) {
    classPaths.addElement(path);
  }

  public Vector<Path> getFileSets() {
    return classPaths;
  }

  public String getClassPath() {
    Path totalPath = null;
    for (Path p : classPaths) {
      if (totalPath == null) {
        totalPath = p;
        continue;
      }
      totalPath.add(p);
    }

    return totalPath != null ? totalPath.toString() : DEFAULT_FILTER;
  }

  public PrintStream getResultPrintStream() {
    try {
      OutputStream os = getOutputStream(resultFile);

      return new PrintStream(os);
    } catch (FileNotFoundException e) {
      throw new BuildException(ERROR_RESULT_FILE_CREATION_FAILED);
    }
  }

  public PrintStream getErrorPrintStream() {
    try {
      OutputStream os = getOutputStream(errorFile);

      return new PrintStream(os);
    } catch (FileNotFoundException e) {
      throw new BuildException(ERROR_ERROR_FILE_CREATION_FAILED);
    }
  }

  OutputStream getOutputStream(String target) throws FileNotFoundException {
    OutputStream os = null;

    if (target.equals(DEFAULT_RESULT_FILE)) {
      os = System.out;
    } else if (target.equals(DEFAULT_ERROR_FILE)) {
      os = System.err;
    } else {
      os = new FileOutputStream(target);
    }

    return os;
  }


  public boolean validate(List<String> messages)
  {
    boolean allOk = true;

    if (! isPrintDepthSet()) {
      printDepth = DEFAULT_PRINT_DEPTH;
      messages.add(TaskModel.ERROR_PRINT_DEPTH_NOT_SET);
    }

    if (! isMinCostSet()) {
      minCost = DEFAULT_MIN_COST;
      messages.add(TaskModel.ERROR_MIN_COST_NOT_SET);
    }

    if (! isMaxExcellentCostSet()) {
      maxExcellentCost = DEFAULT_MAX_EXCELLENT_COST;
      messages.add(TaskModel.ERROR_MAX_EXCELLENT_COST_NOT_SET);
    }

    if (! isMaxAcceptableCostSet()) {
      maxAcceptableCost = DEFAULT_MAX_ACCEPTABLE_COST;
      messages.add(TaskModel.ERROR_MAX_ACCEPTABLE_COST_NOT_SET);
    }

    if (! isWorstOffenderCountSet()) {
      worstOffenderCount = DEFAULT_WORST_OFFENDER_COUNT;
      messages.add(TaskModel.ERROR_WORST_OFFENDER_COUNT_NOT_SET);
    }

    if (! isGroupingSet()) {
      grouping = DEFAULT_GROUPING;
      messages.add(TaskModel.ERROR_GROUPING_NOT_SET);
    }
    if (! isPrintSet()) {
      print = DEFAULT_PRINT;
      messages.add(TaskModel.ERROR_PRINT_NOT_SET);
    }

    if (! isCyclomaticSet()) {
      cyclomatic = DEFAULT_CYCLOMATIC;
      messages.add(TaskModel.ERROR_CYCLOMATIC_NOT_SET);
    }

    if (! isGlobalSet()) {
      global = DEFAULT_GLOBAL;
      messages.add(TaskModel.ERROR_GLOBAL_NOT_SET);
    }

    if (! isResultFileSet()) {
      resultFile = DEFAULT_RESULT_FILE;
      messages.add(TaskModel.ERROR_RESULT_FILE_NOT_SET);
    }
    if (! isErrorFileSet()) {
      messages.add(TaskModel.ERROR_ERROR_FILE_SET_TO_RESULT_FILE);
      errorFile = resultFile;
    }
    if (! isFilterSet()) {
      filter = DEFAULT_FILTER;
      messages.add(TaskModel.ERROR_FILTER_NOT_SET);
    }
    if (! isWhiteListSet()) {
      whiteList = DEFAULT_WHITE_LIST;
      messages.add(TaskModel.ERROR_WHITE_LIST_NOT_SET);
    }

    if (! isFileSetSet()) {
      allOk = false;
      messages.add(TaskModel.ERROR_FILESET_NOT_SET);
    }

    try {
      getOutputStream(resultFile);
    } catch (FileNotFoundException e) {
      allOk = false;
      messages.add(TaskModel.ERROR_RESULT_FILE_CREATION_FAILED);
    }

    try {
      getOutputStream(errorFile);
    } catch (FileNotFoundException e) {
      allOk = false;
      messages.add(TaskModel.ERROR_ERROR_FILE_CREATION_FAILED);
    }

    return allOk;
  }

  private boolean isWhiteListSet() {
    return whiteList != null;
  }

  private boolean isPrintDepthSet() {
    return printDepth != -1;
  }

  private boolean isMinCostSet() {
    return minCost != -1;
  }

  private boolean isMaxExcellentCostSet() {
    return maxExcellentCost != -1;
  }

  private boolean isMaxAcceptableCostSet() {
    return maxAcceptableCost != -1;
  }

  private boolean isWorstOffenderCountSet() {
    return worstOffenderCount != -1;
  }

  private boolean isGroupingSet() {
    return grouping != null;
  }

  private boolean isPrintSet() {
    return print != null;
  }

  private boolean isCyclomaticSet() {
    return cyclomatic != -1;
  }

  private boolean isGlobalSet() {
    return global != -1;
  }

  private boolean isFileSetSet() {
    return classPaths.size() > 0;
  }
  private boolean isFilterSet() {
    return filter != null;
  }

  private boolean isResultFileSet() {
    return !(resultFile == null || resultFile.equals(""));
  }

  private boolean isErrorFileSet() {
    return !(errorFile == null || errorFile.equals(""));
  }
}
