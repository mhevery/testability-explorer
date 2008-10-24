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

}
