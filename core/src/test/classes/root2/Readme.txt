This directory root, root2/ includes classes that:
- Extend from other classes not contained within root2/
- Reference other classes not contained within root2/

Example:
com.google.test.metric.AutoFieldClearTestCase.class 
extends junit.framework.TestCase, which is not in the classpath within root2/

com.google.test.metric.x.SelfTest.class includes a reference to 
org.objectweb.asm.ClassReader, which is not in the root2/ classpath. 
SelfTest.class also extends com.google.test.metric.ClassRepositoryTestCase, 
which is not in the classpath. 