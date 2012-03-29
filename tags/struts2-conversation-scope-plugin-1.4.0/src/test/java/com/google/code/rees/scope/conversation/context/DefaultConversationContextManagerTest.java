package com.google.code.rees.scope.conversation.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Timer;

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
        mgr.setMonitoringFrequency(10L);
        ConversationContext ctx = mgr.getContext(testName, testId);
        for (int i = 0; i < 1000000; i++)
            ;
        Field timerField = mgr.getClass().getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(mgr);
        timer.cancel();

        mgr = SerializationTestingUtil.getSerializedCopy(mgr);
        assertEquals(ctx, mgr.getContext(testName, testId));
        assertNotNull(ctx.getTimerTask());
        for (int i = 0; i < 1000000; i++)
            ;
    }

}
