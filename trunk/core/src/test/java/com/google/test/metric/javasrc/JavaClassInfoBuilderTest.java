package com.google.test.metric.javasrc;

import junit.framework.TestCase;

public class JavaClassInfoBuilderTest extends TestCase {

	JavaClassInfoBuilder builder = new JavaClassInfoBuilder();

	public void testEmptyClass() throws Exception {
		builder.setPackage(0, "pkg");
		builder.startType(0, "A");
		assertEquals("pkg.A", builder.build().getName());
	}

}
