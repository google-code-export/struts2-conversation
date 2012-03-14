package com.google.code.rees.scope.conversation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * This class is used by a {@link ConversationConfigurationProvider} to
 * determine which actions to associate with
 * conversations.
 * 
 * @author rees.byars
 * 
 */
public interface ConversationArbitrator extends Serializable {

    /**
     * The default suffix is "Controller". So the conversation for a class
     * named <code>MyExampleFlowController</code> would be
     * <code>my-example-flow</code>. Use this setter to use a different suffix.
     * 
     * @param suffix
     */
    public void setActionSuffix(String suffix);

    /**
     * Returns a collection of candidate conversation action methods from a
     * class. Subsequent
     * calls are made to the {@link #getConversations(Class, Method)} to
     * determine
     * which, if any, conversations the method should be included with
     * 
     * @param clazz
     * @return
     */
    public Collection<Method> getCandidateConversationMethods(Class<?> clazz);

    /**
     * returns a collection of conversation names for the method as an
     * intermediate point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.ConversationAction
     *      ConversationAction}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getConversations(Class<?> clazz, Method method);

    /**
     * returns the name to be used as an actionId
     * 
     * @see {@link ConversationAdapter#getActionId()}
     * @param method
     * @return
     */
    public String getName(Method method);

    /**
     * returns a collection of conversation names for the method as a begin
     * point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.BeginConversation
     *      BeginConversation}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getBeginConversations(Class<?> clazz,
            Method method);

    /**
     * returns a collection of conversation names for the method as an end point
     * 
     * @see {@link com.google.code.rees.scope.conversation.annotations.EndConversation
     *      EndConversation}
     * @param clazz
     * @param method
     * @return
     */
    public Collection<String> getEndConversations(Class<?> clazz, Method method);
}
