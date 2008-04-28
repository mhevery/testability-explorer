package com.google.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class TaskModel {

    public static final String ERROR_RESULT_FILE_NOT_SET = "resultfile must be set. either a filepath or System.(out|err). default is System.out";
    public static final String ERROR_FILESET_NOT_SET = "fileset to jar and/or classfile directories must be set";    
    public static final String ERROR_FILTER_NOT_SET = "filter must be set. default is \"\" (all)";
    public static final String ERROR_RESULT_FILE_CREATION_FAILED = "resultfile could not be created";
    public static final String ERROR_ERROR_FILE_CREATION_FAILED = "errorfile could not be created";
    private static final String ERROR_ERROR_FILE_SET_TO_RESULT_FILE = "error file not set. using result file.";

    public static final String DEFAULT_RESULT_FILE = "System.out";
    public static final String DEFAULT_ERROR_FILE = "System.err";
    public static final String DEFAULT_FILTER = "";
    public static final int DEFAULT_MAX_ACCEPTABLE_COST = -1;
    public static final int DEFAULT_WORS_OFFENDER_COUNT = -1;
    private static final int DEFAULT_MAX_EXCELLENT_COST = -1;
    private static final int DEFAULT_MIN_COST = -1;

    private Vector fileSets = new Vector();
    private String failProperty;
    private String resultFile = null;
    private String errorFile = null;
    private int maxExcellentCost = -1;
    private int maxAcceptableCost = -1;
    private String filter = null;
    private int minCost = -1;
    private String print = null;
    private int printDepth = -1;
    private String whiteList = null;
    private int worstOffenderCount = -1;

    public void setMaxAcceptableCost(int cost) {
        maxAcceptableCost = cost;
    }

    public void setMaxExcellentCost(int cost) {
        maxExcellentCost = cost;
    }

    public void setMinCost(int cost) {
        minCost = cost;
    }

    public int getMinCost() {
        return minCost;
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

    public void setWorsOffenderCount(int count) {
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

    String getFilter() {
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

    public void addFileSet(FileSet fs) {
        fileSets.addElement(fs);
    }

    public Vector getFileSets() {
        return fileSets;
    }

    public String getClassPath() {
        String cp = DEFAULT_FILTER;

        for (Enumeration en = fileSets.elements(); en.hasMoreElements(); ) {
            FileSet fs = (FileSet) en.nextElement();
            for (Iterator it = fs.iterator(); it.hasNext(); ) {
                FileResource fr = (FileResource) it.next();
                cp += fr.getFile().getAbsolutePath() + ":";
            }
        }

        cp = removeTrailingColonIfExisting(cp);

        return cp;
    }

    private String removeTrailingColonIfExisting(String cp) {
        if (cp.endsWith(":")) {
            cp = cp.substring(0, cp.length() - 1);
        }
        return cp;
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
        }
        else if (target.equals(DEFAULT_ERROR_FILE)) {
            os = System.err;
        }
        else {
            os = new FileOutputStream(target);
        }

        return os;
    }


    public boolean validate(List<String> messages)
    {
        boolean allOk = true;

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


    private boolean isFileSetSet() {
        return fileSets.size() > 0;
    }

    private boolean isFilterSet() {
        return !(filter == null);
    }

    private boolean isResultFileSet() {
        return !(resultFile == null || resultFile.equals(""));
    }

    private boolean isErrorFileSet() {
        return !(errorFile == null || errorFile.equals(""));
    }
}
