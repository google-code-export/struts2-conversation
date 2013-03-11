package com.github.overengineer.scope.conversation.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.github.overengineer.scope.conversation.expression.ExpressionEvaluationException;
import com.github.overengineer.scope.conversation.expression.Mvel;

public class MvelTest extends EvalTest {
	
	@Before
	public void setUp() {
		super.setUp();
		this.eval = new Mvel();
	}
	
	@Test
	public void testEvaluateWithConvenienceFunctions() throws ExpressionEvaluationException {
		eval.evaluate("begin('oopy', 789, 3)");
        eval.evaluate("get('oopy').sookie = bean2.value");
        eval.evaluate("get('oopy').sookie = bean2.value");
        eval.evaluate("get('oopy').sookie = bean2.value");
        eval.evaluate("get('oopy').sookie = bean2.value");
        assertEquals(this.contextManager.getContext("oopy_conversation", "1").get("sookie"), this.bean2.getValue());
        eval.evaluate("continue('oopy').sookie");
        assertEquals(this.adapter.getViewContext().get("oopy_conversation"), "1");
        eval.evaluate("end('oopy').sookie");
        assertNull(this.contextManager.getContext("oopy_conversation", "1"));
        
        eval.evaluate("begin('oopy', 789, 3)");
        eval.evaluate("get('oopy').sookie = bean2.value;");
        assertEquals(this.contextManager.getContext("oopy_conversation", "2").get("sookie"), this.bean2.getValue());
        eval.evaluate("continue('oopy').sookie");
        assertEquals(this.adapter.getViewContext().get("oopy_conversation"), "2");
        eval.evaluate("end('oopy').sookie");
        assertNull(this.contextManager.getContext("oopy_conversation", "2"));
	}

}
