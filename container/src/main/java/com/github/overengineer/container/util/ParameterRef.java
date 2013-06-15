package com.github.overengineer.container.util;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author rees.byars
 */
public interface ParameterRef extends Serializable {
    Type getType();
}
