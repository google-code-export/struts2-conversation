package com.github.overengineer.container.key;

import java.util.Set;

/**
 * @author rees.byars
 */
public class KeyUtil {

    public static SerializableKey getMatchingKey(Key toMatch, Set<SerializableKey> toMatchFrom) {
        for (SerializableKey key : toMatchFrom) {
            if (key.equals(toMatch)) {
                return key;
            }
        }
        return null;
    }

}
