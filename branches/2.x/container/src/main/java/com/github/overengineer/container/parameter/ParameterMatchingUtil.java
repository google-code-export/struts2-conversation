package com.github.overengineer.container.parameter;

/**
 * @author rees.byars
 */
public class ParameterMatchingUtil {

    public static boolean precedingMatch(Class[] requiredArgs, Class[] candidateArgs) {
        if (requiredArgs.length > candidateArgs.length) {
            return false;
        }
        for (int i =  0; i < requiredArgs.length; i++) {
            if (requiredArgs[i] != candidateArgs[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean trailingMatch(Class[] requiredArgs, Class[] candidateArgs) {
        if (requiredArgs.length > candidateArgs.length) {
            return false;
        }
        int delegateOffset = candidateArgs.length - requiredArgs.length;
        for (int i = requiredArgs.length - 1; i >= 0; i--) {
            if (requiredArgs[i] != candidateArgs[i + delegateOffset]) {
                return false;
            }
        }
        return true;
    }

}
