package com.github.overengineer.scope.conversation.expression.eval;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.DefaultConversationContextFactory;
import com.github.overengineer.scope.conversation.context.DefaultConversationContextManager;
import com.github.overengineer.scope.conversation.context.DefaultJeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.expression.eval.Eval;
import com.github.overengineer.scope.mocks.MockConversationAdapter;

//TODO:  implement conversation end processor shit - force after view?  allow option?
//TODO:  make contexts available in view using expression - add to action context or what?
//TODO:  in-memory context manager and JpaContextManager
//TODO:  string interning
//TODO:  conversations attribute on tags, accept ognl map and place it on stack in place of id map
//TODO:  add active conversation context exploration with three columns:  name/key, class, toString
public abstract class EvalTest {

    protected String mockConversationName = "mock_conversation";
    protected String mockConversationId = "1";
    protected MockHttpServletRequest request = new MockHttpServletRequest();
    protected DefaultConversationContextManager contextManager = new DefaultConversationContextManager();
    protected MockConversationAdapter adapter = MockConversationAdapter.init(request, new DefaultJeeConversationContextManagerProvider() {

        private static final long serialVersionUID = 1L;

        @Override
        public ConversationContextManager getManager(HttpServletRequest request) {
            return contextManager;
        }
    });
    protected Eval eval;
    protected TestBean bean1 = new TestBean();
    protected TestBean bean2 = new TestBean();
    protected Map<String, Object> context = new HashMap<String, Object>();

    @Before
    public void setUp() {

        bean1.setName("supa");
        bean1.setValue(42);

        bean2.setName("dupa");
        bean2.setValue(69);

        context.put(bean1.getName(), bean1);
        context.put(bean2.getName(), bean2);

        request.setParameter(mockConversationName, mockConversationId);

        adapter.setAction(this);

        this.contextManager.setContextFactory(new DefaultConversationContextFactory());
        this.contextManager.createContext(mockConversationName, 1L, 20);
    }

    @After
    public void tearDown() {
        this.contextManager.remove(mockConversationName, mockConversationId);
        this.contextManager.destroy();
    }

    public TestBean getBean1() {
        return this.bean1;
    }

    public TestBean getBean2() {
        return this.bean2;
    }

}
