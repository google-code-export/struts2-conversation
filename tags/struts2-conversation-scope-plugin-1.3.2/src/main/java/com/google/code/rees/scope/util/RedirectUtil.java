package com.google.code.rees.scope.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author rees.byars
 * 
 */
public class RedirectUtil {

    public static String getUrlParamString(String url,
            Map<String, String> viewContext) {
        StringBuilder builder = new StringBuilder(url);
        if (viewContext.size() > 0) {
            if (url.contains("?")) {
                for (Entry<String, String> param : viewContext.entrySet()) {
                    builder.append("&").append(param.getKey()).append("=")
                            .append(param.getValue());
                }
            } else {
                builder.append("?");
                for (Entry<String, String> param : viewContext.entrySet()) {
                    builder.append(param.getKey()).append("=")
                            .append(param.getValue()).append("&");
                }
                builder = builder
                        .delete(builder.length() - 1, builder.length());
            }
        }
        return builder.toString();
    }

}
