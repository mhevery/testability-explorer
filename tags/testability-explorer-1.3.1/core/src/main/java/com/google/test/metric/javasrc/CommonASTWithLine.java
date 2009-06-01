package com.google.test.metric.javasrc;

import antlr.CommonAST;
import antlr.Token;

public class CommonASTWithLine extends CommonAST {
	private static final long serialVersionUID = 5403061472453222979L;

	private int line = 0;
	private int column = 0;

	public void initialize(Token tok) {
		super.initialize(tok);
		line = tok.getLine();
		column = tok.getColumn();
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}
}
