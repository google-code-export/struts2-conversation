package com.google.code.rees.scope.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.ConversationConstants;
import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.code.rees.scope.conversation.annotations.ConversationField;
import com.google.code.rees.scope.conversation.context.ConversationContext;
import com.google.code.rees.scope.expression.Eval;
import com.google.code.rees.scope.expression.Groovy;
import com.google.code.rees.scope.expression.Mvel;
import com.google.code.rees.scope.expression.Ognl;
import com.google.code.rees.scope.expression.Spel;
import com.google.code.rees.scope.mocks.actions.conversation.MockConversationController;
import com.google.code.rees.scope.mocks.beans.TestBean;
import com.google.code.rees.scope.session.SessionField;
import com.google.code.rees.scope.struts2.test.ScopeTestUtil;
import com.google.code.rees.scope.testutil.StrutsSpringScopeTestCase;
import com.google.code.struts2.test.junit.StrutsConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:*applicationContext.xml")
@StrutsConfiguration(locations = "struts-conversation.xml")
public class StrutsConversationIntegrationTest extends
        StrutsSpringScopeTestCase<MockConversationController> {

    static final String CONVERSATION_NAME = "oopy" + ConversationConstants.CONVERSATION_NAME_SUFFIX;
    static final String CONVERSATION_FIELD = "conversationString";

    @ConversationField(conversations = "oopy")
    String conversationString;

    @SessionField
    String chubby;

    @Autowired
    TestBean bean;

    @Test
    public void testContinueRegistration() throws Exception {

        conversationString = "shit";

        this.getActionProxy("/conversation/begin").execute();
        System.out.println("1" + this.getAction().getBean().getEcho());
        
        

        ScopeTestUtil.setConversationIdsOnRequest(request, MockConversationController.class);
        this.getActionProxy("/conversation/do1").execute();
        System.out.println("2" + this.getAction().getBean().getEcho());

        ScopeTestUtil.setConversationIdsOnRequest(request, MockConversationController.class);
        this.getActionProxy("/conversation/do2").execute();
        System.out.println("3" + this.getAction().getBean().getEcho());
        System.out.println(ConversationAdapter.getAdapter().getActionId());

        String id = ConversationUtil.getId("oopy");

        this.getActionProxy("begin").execute();

        this.getActionProxy("/conversation/begin").execute();
        System.out.println(this.getAction().getBean().getEcho());

        request.addParameter("oopy_conversation", id);
        this.getActionProxy("/conversation/do1").execute();
        System.out.println(this.getAction().getBean().getEcho());
        
        for (Entry<String, String> conversationEntry : ConversationAdapter.getAdapter().getRequestContext().entrySet()) {
    		String conversationName = conversationEntry.getKey();
    		ConversationContext conversationContext = ConversationAdapter.getAdapter().getConversationContext(conversationEntry.getKey(), conversationEntry.getValue());
    		ConversationAdapter.getAdapter().getActionContext().put(conversationName, conversationContext);
    	}
        
        
        //eval.evaluate("#conversation = @java.lang.System@out.println(\"asdf\")", ConversationAdapter.getAdapter().getActionContext(), this.getAction());
        //System.out.println(eval.evaluate("#conversation", ConversationAdapter.getAdapter().getActionContext(), this.getAction()));
        
        
        Eval eval = new Ognl();
        System.out.println(eval.evaluate("${#oopy_conversation.conversationString}chai-tea${bean.echo}", ConversationAdapter.getAdapter().getActionContext(), this.getAction()));
        
        eval = new Spel();
        eval.evaluate("${#cGet('oopy')['sookie'] = 'pookie'}", ConversationAdapter.getAdapter().getActionContext(), this.getAction());
        System.out.println(eval.evaluate("${#oopy_conversation['conversationString']}cheeko ${#cGet('oopy')['sookie']} and stuff"));
        System.out.println(eval.evaluate("cheeko ${#cBeg('oopy', 789)['sookie']} and stuff"));
        System.out.println(eval.evaluate("cheeko ${#cEnd('oopy')['sookie']} and stuff"));
        System.out.println(eval.evaluate("cheeko ${#cGet('oopy')['sookie']} and stuff"));
        
        eval = new Mvel();
        System.out.println(eval.evaluate("cheeko ${cGet('oopy').sookie = 'jangalang'} and stuff"));
        System.out.println(eval.evaluate("${oopy_conversation.conversationString}chai-tea${bean.echo}"));
        System.out.println(eval.evaluate("cheeko ${cGet('oopy').sookie} and stuff"));
        eval.evaluate("${gLove = 'oopy'; hh = oopy_conversation.conversationString = cGet(gLove).sookie; System.out.println(hh)}");
        eval.evaluate("${cheeba = cBeg('treeeeeeeeeeee', -100).remainingTime; System.out.println(cheeba)}");
        eval.evaluate("${cheeba = cGet('treeeeeeeeeeee').remainingTime; System.out.println(cheeba)}");
        
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${conversationString}"));
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${#oopy_conversation.conversationString}chai-tea${bean.echo}"));
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${conversationString.equals('initialState') ? chubby : bean.echo}shesaid"));
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${conversationString = chubby}"));
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${conversationString}"));
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${23}"));
        ConversationAdapter.getAdapter().postEvaluate("${#oopy_conversation.cheeba = conversationString}");
        System.out.println(ConversationAdapter.getAdapter().postEvaluate("${#oopy_conversation.cheeba}"));
        
        eval = new Groovy();
        System.out.println(eval.evaluate("${oopy_conversation.conversationString}reeeeeeeeeeeesruuuuuuuuuuules${action.bean.echo}"));
        System.out.println(eval.evaluate("cheeko ${cgGet('oopy').sookie} and stuff"));
        System.out.println(eval.evaluate("cheeko ${cgCon('oopy').sookie} and stuff"));
        System.out.println(eval.evaluate("cheeko ${cgBeg('oopy', 789).sookie} and stuff"));
        System.out.println(eval.evaluate("cheeko ${cgEnd('oopy').sookie} and stuff"));
        
        bean = null;
        conversationString = null;
        request.addParameter("oopy_conversation", id);
        this.getActionProxy("/conversation/do2").execute();
        
        System.out.println(this.getAction().getConversationString());
        System.out.println(conversationString);
        System.out.println(bean.getEcho());
    }

    @Test
    public void testBeforeInvocation() throws Exception {

        this.getActionProxy("/conversation/begin").execute();
        String id1 = ConversationUtil.getId(CONVERSATION_NAME);

        this.getActionProxy("/conversation/begin").execute();
        String id2 = ConversationUtil.getId(CONVERSATION_NAME);

        System.out.println("id:  " + id1);
        System.out.println("id:  " + id2);

        request.addParameter(CONVERSATION_NAME, id1);
        request.addParameter(CONVERSATION_FIELD, "one");
        this.getActionProxy("/conversation/do1").execute();

        request.addParameter(CONVERSATION_NAME, id2);
        request.addParameter(CONVERSATION_FIELD, "two");

        this.getActionProxy("/conversation/do1").execute();

        request.addParameter(CONVERSATION_NAME, id1);
        this.getActionProxy("/conversation/do2").execute();
        assertEquals("one", this.getAction().getConversationString());

        request.addParameter(CONVERSATION_NAME, id2);
        this.getActionProxy("/conversation/do2").execute();
        assertEquals("two", this.getAction().getConversationString());

        request.addParameter(CONVERSATION_NAME, id1);
        this.getActionProxy("/conversation/end").execute();

        this.getActionProxy("/conversation/do2").execute();
        assertEquals("initialState", this.getAction().getConversationString());

    }

    @Test
    public void testCrossActionWorkflowFieldPersistence() throws Exception {
        // TODO
    }

    @Test
    public void testActionWithNoWorkflows() throws Exception {
        // TODO
    }

}
