package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.google.code.rees.scope.testutil.SerializableObjectTest;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;

public class DefaultConversationContextFactoryTest extends
        SerializableObjectTest<DefaultConversationContextFactory> {

    @Test
    public void testSetConversationDuration() throws IOException,
            ClassNotFoundException {
        ConversationContextFactory contextFactory = new DefaultConversationContextFactory();
        contextFactory.setConversationDuration(5L);
        ConversationContext context = contextFactory.create("testName",
                "testId");
        assertEquals(5L, context.getRemainingTime());
        contextFactory = SerializationTestingUtil
                .getSerializedCopy(contextFactory);
        context = contextFactory.create("testName", "testId");
        assertEquals(5L, context.getRemainingTime());
    }

    @Test
    public void testCreate() throws IOException, ClassNotFoundException {
        ConversationContextFactory contextFactory = new DefaultConversationContextFactory();
        ConversationContext context = contextFactory.create("testName",
                "testId");
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
        contextFactory = SerializationTestingUtil
                .getSerializedCopy(contextFactory);
        context = contextFactory.create("testName", "testId");
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
    }
}
