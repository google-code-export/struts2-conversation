package com.google.code.rees.scope.expression;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

//TODO:  add mock adapter and test the conversation functions
//TODO:  add active conversation context exploration with three columns:  name/key, class, toString
public abstract class EvalTest {
	
	protected Eval eval;
	private TestBean bean1 = new TestBean();
	private TestBean bean2 = new TestBean();
	Map<String, Object> context = new HashMap<String, Object>();
	
	@Before
	public void setUp() {
		bean1.setName("supa");
		bean1.setValue(42);
		
		bean2.setName("dupa");
		bean2.setValue(69);
		
		context.put(bean1.getName(), bean1);
		context.put(bean2.getName(), bean2);
	}
	
	public TestBean getBean1() {
		return this.bean1;
	}
	
	public TestBean getBean2() {
		return this.bean2;
	}

}
