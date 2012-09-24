package com.google.code.rees.scope.expression;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;

public class Groovy implements Eval {

	private static final Logger LOG = LoggerFactory.getLogger(Groovy.class);
	private static final String CONVERSATION_ACCESSOR = 
			"<% cGet = { c_name -> com.google.code.rees.scope.conversation.ConversationUtil.getContextUsingSimpleName(c_name) } %>";
	private static final String CONVERSATION_TERMINATOR = 
			"<% cEnd = { c_name -> com.google.code.rees.scope.conversation.ConversationUtil.endUsingSimpleName(c_name) } %>";
	private static final String CONVERSATION_INITIATOR = 
			"<% cBeg = { c_name, c_len, c_max -> com.google.code.rees.scope.conversation.ConversationUtil.beginUsingSimpleName(c_name, c_len, c_max) } %>";
	private static final String CONVERSATION_CONTINUATOR = 
			"<% cCon = { c_name -> com.google.code.rees.scope.conversation.ConversationUtil.persistUsingSimpleName(c_name) } %>";
	
	private final SimpleTemplateEngine engine = new SimpleTemplateEngine();
	private final Map<String, Template> templateCache = new ConcurrentHashMap<String, Template>();

	@Override
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) {
		Template template = this.templateCache.get(expression);
		if (template == null) {
			LOG.debug("Compiled template not found in cache, compiling template and caching.");
			try {
				template = engine.createTemplate(CONVERSATION_ACCESSOR + CONVERSATION_TERMINATOR + CONVERSATION_INITIATOR + CONVERSATION_CONTINUATOR + expression);
			} catch (CompilationFailedException e) {
				LOG.error("Could not compile template.  Message:  " + e.getMessage());
			} catch (ClassNotFoundException e) {
				LOG.error("Could not compile template.  Message:  " + e.getMessage());
			} catch (IOException e) {
				LOG.error("Could not compile template.  Message:  " + e.getMessage());
			}
			this.templateCache.put(expression, template);
		}
		evaluationContext.put("action", root);
		return template.make(evaluationContext).toString();
	}

	@Override
	public Object evaluate(String expression) {
		return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
	}
	
}
