package com.github.overengineer.scope.container;


@Prototype
public class Bean implements IBean {

    @Component
    public void setBean(IBean2 bean2){
        bean2.doStuff();
    }

}
