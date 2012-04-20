package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.google.code.rees.scope.testutil.SerializableObjectTest;
import com.google.code.rees.scope.testutil.SerializationTestingUtil;

public class DefaultConversationContextTest extends
        SerializableObjectTest<DefaultConversationContext> {

    @Test
    public void testDefaultConversationContext() throws IOException,
            ClassNotFoundException {
        ConversationContext context = new DefaultConversationContext(
                "testName", "testId", 5L);
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
        assertEquals(5L, context.getRemainingTime());
        context.put("bean", "beanValue");
        assertEquals("beanValue", context.get("bean"));
        context = SerializationTestingUtil.getSerializedCopy(context);
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
        assertEquals("beanValue", context.get("bean"));
        assertEquals(5L, context.getRemainingTime());
    }

}
