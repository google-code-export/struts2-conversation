package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.TimerTask;

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

    @Test
    public void testSetAndGetTimerTask() {
        ConversationContext context = new DefaultConversationContext(
                "testName", "testId", 5L);
        TestTimerTask task = new TestTimerTask();
        context.setTimerTask(task);
        assertEquals(task, context.getTimerTask());
    }

    class TestTimerTask extends TimerTask {
        @Override
        public void run() {
            // blah blah blah
        }
    }

}
