package com.github.overengineer.scope.container.proxy.aop;

import com.github.overengineer.scope.container.*;
import com.github.overengineer.scope.container.proxy.*;
import com.github.overengineer.scope.container.type.DefaultKeyGenerator;
import com.github.overengineer.scope.container.type.GenericKey;
import com.github.overengineer.scope.container.type.KeyGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rees.byars
 */
public class AopModule extends BaseModule {

    public AopModule() {
        use(JdkProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(DefaultComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(ProxyComponentStrategyFactory.class).forType(ComponentStrategyFactory.class);
        use(DefaultKeyGenerator.class).forType(KeyGenerator.class);
        use(JdkAopProxyHandlerFactory.class).forType(ProxyHandlerFactory.class);
        use(AdvisedInvocationFactory.class).forType(JoinPointInvocationFactory.class);
        use(DefaultPointcutInterpreter.class).forType(PointcutInterpreter.class);
        use(DefaultAopContainer.class).forType(Container.class);
        use(DefaultAopContainer.class).forType(HotSwappableContainer.class);
        use(DefaultAopContainer.class).forType(AopContainer.class);
        useInstance(new ArrayList<Aspect>()).forGeneric(new GenericKey<List<Aspect>>(){});
    }
}
