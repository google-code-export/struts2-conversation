package com.google.code.rees.scope.expression;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SpelTest extends EvalTest {
	
	@Before
	public void setUp() {
		super.setUp();
		this.eval = new Spel();
	}
	
	@Test
	public void testEvaluate() {
		String expression = "total = ${#supa.value - #dupa.value}${bean1.name}";
		assertEquals("total = -27supa", eval.evaluate(expression, context, this));
	}

}
