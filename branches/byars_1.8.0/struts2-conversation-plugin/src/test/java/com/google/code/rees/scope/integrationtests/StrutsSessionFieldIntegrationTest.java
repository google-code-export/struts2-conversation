package com.google.code.rees.scope.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.components.Text;
import org.apache.struts2.views.freemarker.tags.TextAreaModel;
import org.junit.Test;

import com.google.code.rees.scope.mocks.actions.MockConventionController;
import com.google.code.rees.scope.mocks.actions.MockPojoController;
import com.google.code.rees.scope.testutil.StrutsScopeTestCase;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class StrutsSessionFieldIntegrationTest extends
        StrutsScopeTestCase<MockConventionController> {

    @Test
    public void testBeforeInvocation() throws Exception {

        request.setParameter("abstractField", "hello");
        ActionProxy proxy = this.getActionProxy("begin");
        
        assertEquals("hola", this.getAction().getAbstractField());
        assertEquals(2, this.getAction().getDumb());
        assertEquals(2, this.getAction().getSmart());
        assertEquals(MockConventionController.NOT_IN_PROGRESS, this.getAction()
                .getSessionString());
        proxy.execute();
        assertEquals("hello", this.getAction().getAbstractField());
        assertEquals(3, this.getAction().getDumb());
        assertEquals(3, this.getAction().getSmart());
        assertEquals(MockConventionController.IN_PROGRESS, this.getAction()
                .getSessionString());
        this.getActionProxy("continue1").execute();
        this.getActionProxy("continue2").execute();
        assertEquals(2, this.getAction().getDumb());
        assertEquals(3, this.getAction().getSmart());
        assertEquals(MockConventionController.IN_PROGRESS, this.getAction()
                .getSessionString());
        request.setParameter("smart", "145");
        this.getActionProxy("end").execute();
        assertEquals(MockConventionController.NOT_IN_PROGRESS, this.getAction()
                .getSessionString());
        assertEquals(145, this.getAction().getSmart());
        assertEquals("hello", this.getAction().getAbstractField());
    }

    @Test
    public void testCrossActionSessionFieldPersistence() throws Exception {
        request.addParameter("sessionField", "test");
        this.getActionProxy("configuration-action").execute();
        this.getActionProxy("mock-pojo").execute();
        assertEquals("test", getAction(MockPojoController.class)
                .getSessionField());
    }

    @Test
    public void testActionWithNoSessionFields() throws Exception {
        this.getActionProxy("mock-no-session-field").execute();
        ActionProxy proxy = this.getActionProxy("configuration-action");
        System.out.println(proxy.getActionName());
        System.out.println(proxy.getConfig().getName());
        HashMap map = new HashMap();
        map.put("class", "shit");
        
        this.copyParams(map, ActionContext.getContext().getValueStack());
        
        System.out.println(ActionContext.getContext().getValueStack().findValue("class"));
    }
    
    public void copyParams(Map params, ValueStack stack) {
    	stack.push(params);
    	//stack.push(this);
    	try {
    		            for (Object o : params.entrySet()) {
    	                Map.Entry entry = (Map.Entry) o;
    	                String key = (String) entry.getKey();
    		                stack.setValue(key, entry.getValue());
    		            }
    		        } finally {
    	            stack.pop();
    	            //stack.pop();
    	        }
    		    }

}
