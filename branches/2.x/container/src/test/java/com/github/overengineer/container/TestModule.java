package com.github.overengineer.container;

/**
 */
public class TestModule extends BaseModule {

    public TestModule() {

        use(Bean.class).forType(IBean.class);


    }

}
