package com.google.code.rees.scope.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class OgnlTest extends EvalTest {
	
	@Before
	public void setUp() {
		super.setUp();
		this.eval = new Ognl();
	}
	
	@Test
	public void testEvaluate() {
		String expression = "total = ${#supa.value + #dupa.value}${bean1.name}";
		assertEquals("total = 111supa", eval.evaluate(expression, context, this));
	}
	
	@Test
	public void testEvaluateWithConvenienceFunctions() {
		eval.evaluate("ginger ${#c.beg('oopy', 789)} and stuff");
		eval.evaluate("ginger ${#c.beg('zoopy', 789)} and stuff");
		assertEquals(this.contextManager.getContext("oopy_conversation", "1").getRemainingTime(), 789L);
        eval.evaluate("ginger ${#c.get('oopy').sookie = bean2.value} and stuff");
        assertEquals(this.contextManager.getContext("oopy_conversation", "1").get("sookie"), this.bean2.getValue());
        eval.evaluate("ginger ${#c.con('oopy').sookie} and stuff");
        assertEquals(this.adapter.getViewContext().get("oopy_conversation"), "1");
        eval.evaluate("${#c.get('zoopy').mookie = #c.end('oopy').sookie}");
        assertNull(this.contextManager.getContext("oopy_conversation", "1"));
        assertEquals(this.contextManager.getContext("zoopy_conversation", "2").get("mookie"), this.bean2.getValue());
	}

}
