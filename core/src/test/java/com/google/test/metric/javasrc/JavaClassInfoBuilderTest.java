package com.google.test.metric.javasrc;

import junit.framework.TestCase;

public class JavaClassInfoBuilderTest extends TestCase {

	JavaClassInfoBuilder builder = new JavaClassInfoBuilder();
	
	public void testEmptyClass() throws Exception {
		builder.setPackage("pkg");
		builder.startType("A");
		assertEquals("pkg.A", builder.build().getName());
	}

}
