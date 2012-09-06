package com.google.code.rees.scope.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rees.scope.conversation.ConversationAdapter;

public class Mvel implements Eval {
	
	private static final Logger LOG = LoggerFactory.getLogger(Mvel.class);
	private static final String CONVERSATION_ACCESSOR = 
			"${ if (isdef cGet) { return '';} def cGet(c_name) { com.google.code.rees.scope.conversation.ConversationUtil.getContextUsingSimpleName(c_name); } return ''; }";
	private static final String CONVERSATION_TERMINATOR = 
			"${ if (isdef cEnd) { return '';} def cEnd(c_name) { com.google.code.rees.scope.conversation.ConversationUtil.endUsingSimpleName(c_name); } return ''; }";
	private static final String CONVERSATION_INITIATOR = 
			"${ if (isdef cBeg) { return '';} def cBeg(c_name, c_len) { com.google.code.rees.scope.conversation.ConversationUtil.beginUsingSimpleName(c_name, c_len); } return ''; }";
	private static final String CONVERSATION_CONTINUATOR = 
			"${ if (isdef cCon) { return '';} def cCon(c_name) { com.google.code.rees.scope.conversation.ConversationUtil.persistUsingSimpleName(c_name); } return ''; }";
	
	private final Map<String, CompiledTemplate> templateCache = new ConcurrentHashMap<String, CompiledTemplate>();

	@Override
	public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) {
		CompiledTemplate template = this.templateCache.get(expression);
		if (template == null) {
			LOG.debug("Compiled template not found in cache, compiling template and caching.");
			template = TemplateCompiler.compileTemplate(CONVERSATION_ACCESSOR + CONVERSATION_TERMINATOR + CONVERSATION_INITIATOR + CONVERSATION_CONTINUATOR + expression);
			this.templateCache.put(expression, template);
		}
		return TemplateRuntime.execute(template, root, evaluationContext);
	}

	@Override
	public Object evaluate(String expression) {
		return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
	}
}
