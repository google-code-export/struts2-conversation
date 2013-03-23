package com.github.overengineer.scope.integrationtests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.ConversationConstants;
import com.github.overengineer.scope.conversation.ConversationUtil;
import com.github.overengineer.scope.conversation.annotations.ConversationField;
import com.github.overengineer.scope.mocks.actions.conversation.MockConversationController;
import com.github.overengineer.scope.mocks.beans.TestBean;
import com.github.overengineer.scope.session.SessionField;
import com.github.overengineer.scope.struts2.test.ScopeTestUtil;
import com.github.overengineer.scope.testutil.StrutsSpringScopeTestCase;
import com.google.code.struts2.test.junit.StrutsConfiguration;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.TextParseUtil;

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

        bean = null;
        conversationString = null;
        request.addParameter("oopy_conversation", id);
        this.getActionProxy("/conversation/do2").execute();

        System.out.println(this.getAction().getConversationString());
        System.out.println(conversationString);
        System.out.println(bean.getEcho());

        System.out.println(TextParseUtil.translateVariables('$', "total = ${77 * 99}", ActionContext.getContext().getValueStack()));
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

}
