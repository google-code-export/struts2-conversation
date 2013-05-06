package com.github.overengineer.container;


import com.google.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@Prototype
public class Bean implements IBean {

    @Autowired
    @Inject
    public Bean(IBean2 bean2){
        bean2.doStuff();
    }

    @Override
    public void stuff() {
    }
}
