package com.github.overengineer.scope.expression;

import java.util.List;

/**
 * Used to parse templates of mixed text and expressions
 * 
 * @author reesbyars
 *
 */
public interface TemplateParser {
	
	/**
	 * 
	 * @param templateText
	 * @return a list of String expressions whereby the text (i.e. not expressions within the designated prefix/suffix pair)
	 * is converted to text within single quotes and the expressions are returned as the Strings from within the prefix/suffix pairs
	 */
	public List<String> parse(String templateText);

}
