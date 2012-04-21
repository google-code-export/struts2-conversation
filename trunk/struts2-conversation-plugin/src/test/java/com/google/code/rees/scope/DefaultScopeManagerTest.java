package com.google.code.rees.scope;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.code.rees.scope.conversation.ConversationAdapter;
import com.google.code.rees.scope.conversation.processing.ConversationManager;
import com.google.code.rees.scope.session.SessionAdapter;
import com.google.code.rees.scope.session.SessionManager;

public class DefaultScopeManagerTest {

    @Test
    public void testProcessScopes() {

        ConversationManager cManager = EasyMock
                .createMock(ConversationManager.class);
        ConversationAdapter cAdapter = EasyMock
                .createMock(ConversationAdapter.class);
        cManager.processConversations(cAdapter);
        EasyMock.expectLastCall();
        EasyMock.replay(cManager);

        SessionManager sManager = EasyMock.createMock(SessionManager.class);
        SessionAdapter sAdapter = EasyMock.createMock(SessionAdapter.class);
        sManager.processSessionFields(sAdapter);
        EasyMock.expectLastCall();
        EasyMock.replay(sManager);

        ScopeAdapterFactory adapterFactory = EasyMock
                .createMock(ScopeAdapterFactory.class);
        EasyMock.expect(adapterFactory.createConversationAdapter()).andReturn(
                cAdapter);
        EasyMock.expect(adapterFactory.createSessionAdapter()).andReturn(
                sAdapter);
        EasyMock.replay(adapterFactory);

        ScopeManager scopeManager = new DefaultScopeManager();
        scopeManager.setConversationManager(cManager);
        scopeManager.setSessionManager(sManager);
        scopeManager.setScopeAdapterFactory(adapterFactory);

        scopeManager.processScopes();

        EasyMock.verify(adapterFactory);
        EasyMock.verify(sManager);
        EasyMock.verify(cManager);

    }

}
