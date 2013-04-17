package com.github.overengineer.scope.container.proxy.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author rees.byars
 */
public interface PointcutInterpreter extends Serializable {

    boolean appliesToMethod(Aspect aspect, Class targetClass, Method method);

}
