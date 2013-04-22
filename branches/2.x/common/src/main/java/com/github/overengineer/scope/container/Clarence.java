package com.github.overengineer.scope.container;

import com.github.overengineer.scope.container.proxy.HotSwappableContainer;
import com.github.overengineer.scope.container.proxy.ProxyModule;
import com.github.overengineer.scope.container.proxy.aop.AopContainer;
import com.github.overengineer.scope.container.proxy.aop.AopModule;

/**
 * @author rees.byars
 */
public class Clarence {

    Container builder = new DefaultContainer(new DefaultComponentStrategyFactory());

    public static Clarence please() {
        return new Clarence();
    }

    public HotSwappableContainer gimmeThatProxyTainer() {
        return builder.loadModule(ProxyModule.class).get(HotSwappableContainer.class);
    }

    public AopContainer gimmeThatAopTainer() {
        return builder.loadModule(AopModule.class).get(AopContainer.class);
    }

    public Container gimmeThatTainer() {
        return builder;
    }

}
