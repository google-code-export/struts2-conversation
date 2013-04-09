package com.github.overengineer.scope.container;


import com.google.inject.Inject;

@Prototype
public class Bean implements IBean {

    @Inject
    public Bean(IBean2 bean2){
        bean2.doStuff();
    }

}
