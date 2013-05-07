package com.github.overengineer.container;

/**
 */
public class TestModule extends BaseModule {

    @Override
    protected void configure() {

        use(Bean.class).forType(IBean.class);


    }

}
