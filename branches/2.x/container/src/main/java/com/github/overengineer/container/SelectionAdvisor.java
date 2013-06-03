package com.github.overengineer.container;

/**
 * @author rees.byars
 */
public interface SelectionAdvisor {
    boolean validSelection(Class<?> candidateClass);
}
