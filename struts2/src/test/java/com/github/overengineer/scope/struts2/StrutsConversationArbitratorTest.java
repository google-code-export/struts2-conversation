package com.github.overengineer.scope.struts2;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.github.overengineer.scope.ActionProvider;
import com.github.overengineer.scope.mocks.actions.MockConventionController;
import com.github.overengineer.scope.mocks.actions.conversation.MockConversationController;
import com.github.overengineer.scope.mocks.actions.conversation.MockConversationControllerInterface;
import com.github.overengineer.scope.struts2.StrutsConversationArbitrator;

public class StrutsConversationArbitratorTest {

    @Test
    public void testGetPackageBasedConversations() {

        StrutsConversationArbitrator arbitrator = new StrutsConversationArbitrator();
        ActionProvider actionProvider = new ActionProvider() {

            private static final long serialVersionUID = 1L;

            @Override
            public Set<Class<?>> getActionClasses() {
                Set<Class<?>> classes = new HashSet<Class<?>>();
                classes.add(MockConventionController.class);
                classes.add(MockConversationController.class);
                return classes;
            }

        };
        arbitrator.setUsePackageNesting("true");
        arbitrator.setActionProvider(actionProvider);
        assertTrue(arbitrator.getPackageBasedConversations(
                MockConversationControllerInterface.class, "Controller")
                .contains("mock-convention"));
    }

}
