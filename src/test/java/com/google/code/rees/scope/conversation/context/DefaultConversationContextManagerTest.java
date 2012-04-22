package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.rees.scope.testutil.SerializationTestingUtil;

public class DefaultConversationContextManagerTest {

    @Test
    public void testGetContext() {

    }

    @Test
    public void testSerialization() throws Exception {

        String testName = "test-conversation";
        String testId = "testId";
        DefaultConversationContextManager mgr = new DefaultConversationContextManager();
        mgr.setContextFactory(new DefaultConversationContextFactory());
        ConversationContext ctx = mgr.getContext(testName, testId);

        mgr = SerializationTestingUtil.getSerializedCopy(mgr);
        assertEquals(ctx, mgr.getContext(testName, testId));

    }

}
