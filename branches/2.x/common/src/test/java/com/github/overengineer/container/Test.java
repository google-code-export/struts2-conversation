package com.github.overengineer.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

public class Test {

    List<String> stringList;
    List<Integer> integerList;

    public Test(Map<String, List<String>> strings, Map<String, List<String>> strings2, Map<String, String> strings3) {

    }

    public static void main(String... args) throws Exception {
        Field stringListField = Test.class.getDeclaredField("stringList");
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        System.out.println(stringListClass); // class java.lang.String.

        Field integerListField = Test.class.getDeclaredField("integerList");
        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
        System.out.println(integerListClass); // class java.lang.Integer.

        Constructor c = Test.class.getConstructor(Map.class, Map.class, Map.class);
        ParameterizedType p = (ParameterizedType) c.getGenericParameterTypes()[0];
        System.out.println(((ParameterizedType) p.getActualTypeArguments()[1]).getActualTypeArguments()[0]);

    }
}