/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric.javasrc;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.google.classpath.ClassPath;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;

public class JavaSrcRepository implements ClassRepository {

	private final ClassPath classPath;

	public JavaSrcRepository(ClassPath classPath) {
		this.classPath = classPath;
	}

	public ClassInfo getClass(String clazzName) {
		String src = clazzName.replace('.', '/') + ".java";
		JavaLexer lexer = new JavaLexer(classPath.getResourceAsStream(src));
		JavaRecognizer recognizer = new JavaRecognizer(lexer);
		recognizer.getASTFactory().setASTNodeClass(CommonASTWithLine.class);
		JavaTreeParser treeParser = new JavaTreeParser();
		try {
			recognizer.compilationUnit();
			treeParser.compilationUnit(recognizer.getAST());
			return treeParser.builder.build();
		} catch (RecognitionException e) {
			throw new RuntimeException(e);
		} catch (TokenStreamException e) {
			throw new RuntimeException(e);
		}
	}

}
