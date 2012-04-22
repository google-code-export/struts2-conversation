package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.google.code.rees.scope.testutil.SerializableObjectTest;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;

public class DefaultConversationContextFactoryTest extends
        SerializableObjectTest<DefaultConversationContextFactory> {

    @Test
    public void testCreate() throws IOException, ClassNotFoundException {
    	final long duration = 500000L;
        final long elapsedTimeWindow = 1000L;
        ConversationContextFactory contextFactory = new DefaultConversationContextFactory();
        ConversationContext context = contextFactory.create("testName",
                "testId", duration);
        assertTrue(context.getRemainingTime() <= duration
                && context.getRemainingTime() > duration - elapsedTimeWindow);
        contextFactory = SerializationTestingUtil
                .getSerializedCopy(contextFactory);
        context = contextFactory.create("testName", "testId", duration);
        assertTrue(context.getRemainingTime() <= duration
                && context.getRemainingTime() > duration - elapsedTimeWindow);
    }
}
