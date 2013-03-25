package com.github.overengineer.scope.container.standalone;

import com.github.overengineer.scope.container.ScopeContainer;
import org.junit.Test;

/**
 */
public class SimpleScopeContainerTest {


    @Test
    public void testVerify() throws WiringException {

        StandaloneContainer container = new SimpleScopeContainer();

        container.addInstance(ScopeContainer.class, container);

        container.verify();

    }

}
