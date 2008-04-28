package com.google.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskModelTest extends TestCase {
    private TaskModel model;

    public TaskModelTest(String name)
    {
        super(name);
    }

    protected void setUp()
    {
        model = new TaskModel();
    }

    protected void tearDown()
    {
    }

    public void testDefaultResultFile() throws Exception {
        assertEquals(null, model.getResultFile());
    }

    public void testDefaultErrorFile() throws Exception {
        assertEquals(null, model.getErrorFile());
    }

    public void testDefaultFilter() throws Exception {
        assertEquals(null, model.getFilter());
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
        assertTrue(model.getClassPath().indexOf(":") >= 0);
        String[] cps = model.getClassPath().split(":");
        assertEquals(2, cps.length);
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

