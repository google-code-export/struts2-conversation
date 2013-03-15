package com.github.overengineer.scope.conversation.expression.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.github.overengineer.scope.conversation.expression.eval.Spel;
import com.github.overengineer.scope.conversation.expression.exceptions.ExpressionEvaluationException;

public class SpelTest extends EvalTest {
	
	@Before
	public void setUp() {
		super.setUp();
		this.eval = new Spel();
	}
	
	@Test
	public void testEvaluate() throws ExpressionEvaluationException {
		String expression = "total = ${#supa.value + #dupa.value}${bean1.name}";
		assertEquals("total = 111supa", eval.evaluate(expression, context, this));
	}
	
	@Test
	public void testEvaluateWithConvenienceFunctions() throws ExpressionEvaluationException {
		eval.evaluate("${#cBeg('oopy', 789, 3)}");
		assertEquals(this.contextManager.getContext("oopy_conversation", "1").getRemainingTime(), 789L);
        eval.evaluate("ginger ${#cGet('oopy')['sookie'] = bean2.value} and stuff");
        assertEquals(this.contextManager.getContext("oopy_conversation", "1").get("sookie"), this.bean2.getValue());
        eval.evaluate("ginger ${#cCon('oopy')['sookie']} and stuff");
        assertEquals(this.adapter.getViewContext().get("oopy_conversation"), "1");
        eval.evaluate("stfu ${#cEnd('oopy')['sookie']} and stuff");
        assertNull(this.contextManager.getContext("oopy_conversation", "1"));
	}

}
