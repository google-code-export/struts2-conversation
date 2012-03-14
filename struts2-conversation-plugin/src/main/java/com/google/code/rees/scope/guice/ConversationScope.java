package com.google.code.rees.scope.guice;

import com.google.code.rees.scope.conversation.ConversationUtil;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author rees.byars
 */
public class ConversationScope implements Scope {

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {

        return new Provider<T>() {

            public T get() {

                String contextKey = String.valueOf(key.hashCode());

                @SuppressWarnings("unchecked")
                T value = (T) ConversationUtil.getField(contextKey);

                if (value == null) {
                    value = provider.get();
                    ConversationUtil.setField(contextKey, value);
                }

                return value;
            }
        };
    }

}
