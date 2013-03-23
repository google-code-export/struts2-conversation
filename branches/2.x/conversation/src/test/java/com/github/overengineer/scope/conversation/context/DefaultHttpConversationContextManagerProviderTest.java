package com.github.overengineer.scope.conversation.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.github.overengineer.scope.container.ScopeContainer;
import com.github.overengineer.scope.conversation.context.ConversationContextManager;
import com.github.overengineer.scope.conversation.context.DefaultJeeConversationContextManagerProvider;
import com.github.overengineer.scope.conversation.context.JeeConversationContextManagerProvider;
import com.github.overengineer.scope.testutil.SerializableObjectTest;
import com.github.overengineer.scope.testutil.SerializationTestingUtil;

public class DefaultHttpConversationContextManagerProviderTest extends
        SerializableObjectTest<DefaultJeeConversationContextManagerProvider> implements Serializable {

    @Test
    public void testGetManager() throws IOException, ClassNotFoundException {

        DefaultJeeConversationContextManagerProvider managerProvider = new DefaultJeeConversationContextManagerProvider();

        managerProvider.setScopeContainer(new ScopeContainer() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> T getComponent(Class<T> clazz) {
                return (T) new DefaultConversationContextManager();
            }

            @Override
            public <T> T getProperty(Class<T> clazz, String name) {
                // TODO Auto-generated method stub
                return null;
            }

        });

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        MockHttpSession session1 = new MockHttpSession();
        request1.setSession(session1);
        ConversationContextManager manager1 = managerProvider
                .getManager(request1);
        assertNotNull(manager1);

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpSession session2 = new MockHttpSession();
        request2.setSession(session2);
        ConversationContextManager manager2 = managerProvider
                .getManager(request2);
        assertNotNull(manager2);

        assertNotSame(manager1, manager2);

        request1 = new MockHttpServletRequest();
        request1.setSession(session1);
        ConversationContextManager manager11 = managerProvider
                .getManager(request1);
        assertEquals(manager1, manager11);

        managerProvider = SerializationTestingUtil
                .getSerializedCopy(managerProvider);

        request1 = new MockHttpServletRequest();
        request1.setSession(session1);
        manager11 = managerProvider.getManager(request1);
        assertEquals(manager1, manager11);

    }

}
