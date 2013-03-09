package com.github.overengineer.scope.conversation.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.github.overengineer.scope.conversation.context.ConversationContext;
import com.github.overengineer.scope.conversation.context.DefaultConversationContext;
import com.github.overengineer.scope.testutil.SerializableObjectTest;
import com.github.overengineer.scope.testutil.SerializationTestingUtil;

public class DefaultConversationContextTest extends SerializableObjectTest<DefaultConversationContext> {

    @Test
    public void testDefaultConversationContext() throws IOException, ClassNotFoundException {
        
    	ConversationContext context = new DefaultConversationContext("testName", "testId", 5L);
        assertTrue(5L == context.getRemainingTime() || 4L == context.getRemainingTime());
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
        context.put("bean", "beanValue");
        assertEquals("beanValue", context.get("bean"));
        context = SerializationTestingUtil.getSerializedCopy(context);
        assertEquals("testName", context.getConversationName());
        assertEquals("testId", context.getId());
        assertEquals("beanValue", context.get("bean"));
        assertEquals(5L, context.getRemainingTime());
        
        context.put("bean", null);
        assertNull(context.get("bean"));
    }

}
