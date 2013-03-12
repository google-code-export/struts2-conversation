package com.github.overengineer.scope.conversation.expression.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * not fast at parsing, but effective for our needs and fast enough through caching 
 * 
 * @author reesbyars
 *
 */
public class SimpleCachingTemplateParser implements TemplateParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(SimpleCachingTemplateParser.class);
	private final Map<String, List<String>> preparsedCache = new ConcurrentHashMap<String, List<String>>();
	private Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
	
	public SimpleCachingTemplateParser() {
	}
	
	public SimpleCachingTemplateParser(String prefix, String suffix) {
		this.pattern = Pattern.compile("(" + prefix + ")(.*?)(" + suffix + ")");
	}
	
	/**
	 * converts the template text into a list of expressions.  nested expressions of the form <code>${..${..}..}</code>
	 * are not respected.  
	 * 
	 * @param expression
	 * @return
	 */
	public List<String> parse(String templateText) {
    	
		List<String> parsedExpressions = null;
    	
		parsedExpressions = preparsedCache.get(templateText);
    	
    	if (parsedExpressions == null) {
    		
    		if (LOG.isDebugEnabled()) {
    			LOG.debug("Parsing template:  " + templateText);
    		}
    		
    		parsedExpressions = new ArrayList<String>();
    		
    		Matcher matcher = pattern.matcher(templateText);
            
            int endOfLast = 0;
            while(matcher.find()) {
            	
            	int newStart = matcher.start();
            	
            	//plain text
            	if (endOfLast < newStart - 1) {
            		parsedExpressions.add("'" + templateText.substring(endOfLast, newStart) + "'");
            	}
            	
            	//expression text
            	parsedExpressions.add(matcher.group(2));
            	
            	endOfLast = matcher.end();
            }
            
            //no expressions found, add all as plain text
            if (endOfLast == 0) {
            	parsedExpressions.add("'" + templateText + "'");
            	
            //trailing plain text
            } else if (endOfLast < templateText.length()) {
            	parsedExpressions.add("'" + templateText.substring(endOfLast, templateText.length()) + "'");
        	}
            
            preparsedCache.put(templateText, parsedExpressions);
    		
    	}
        
        return parsedExpressions;
	}

}
