package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.google.code.rees.scope.testutil.SerializableObjectTest;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;

public class DefaultHttpConversationContextManagerProviderTest extends
        SerializableObjectTest<DefaultHttpConversationContextManagerProvider> {

    @Test
    public void testGetManager() throws IOException, ClassNotFoundException {

        HttpConversationContextManagerProvider managerProvider = new DefaultHttpConversationContextManagerProvider();

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
