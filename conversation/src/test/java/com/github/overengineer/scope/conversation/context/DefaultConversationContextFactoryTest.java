package com.github.overengineer.scope.conversation.context;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.github.overengineer.scope.conversation.context.ConversationContext;
import com.github.overengineer.scope.conversation.context.ConversationContextFactory;
import com.github.overengineer.scope.conversation.context.DefaultConversationContextFactory;
import com.github.overengineer.scope.testutil.SerializableObjectTest;
import com.github.overengineer.scope.testutil.SerializationTestingUtil;

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
