package com.github.overengineer.scope.conversation.expression.eval;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.overengineer.scope.conversation.ConversationAdapter;
import com.github.overengineer.scope.conversation.expression.exceptions.ExpressionEvaluationException;

/**
 * @author reesbyars
 */
public class Groovy implements Eval {

    private static final Logger LOG = LoggerFactory.getLogger(Groovy.class);
    private static final String CONVERSATION_ACCESSOR =
            "<% cGet = { c_name -> com.github.overengineer.scope.conversation.ConversationUtil.getContextUsingSimpleName(c_name) } %>";
    private static final String CONVERSATION_TERMINATOR =
            "<% cEnd = { c_name -> com.github.overengineer.scope.conversation.ConversationUtil.endUsingSimpleName(c_name) } %>";
    private static final String CONVERSATION_INITIATOR =
            "<% cBeg = { c_name, c_len, c_max -> com.github.overengineer.scope.conversation.ConversationUtil.beginUsingSimpleName(c_name, c_len, c_max) } %>";
    private static final String CONVERSATION_CONTINUATOR =
            "<% cCon = { c_name -> com.github.overengineer.scope.conversation.ConversationUtil.persistUsingSimpleName(c_name) } %>";

    private final SimpleTemplateEngine engine = new SimpleTemplateEngine();
    private final Map<String, Template> templateCache = new ConcurrentHashMap<String, Template>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(String expression, Map<String, Object> evaluationContext, Object root) throws ExpressionEvaluationException {
        try {
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
        } catch (Exception e) {
            throw new ExpressionEvaluationException(expression, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(String expression) throws ExpressionEvaluationException {
        try {
            return this.evaluate(expression, ConversationAdapter.getAdapter().getActionContext(), ConversationAdapter.getAdapter().getAction());
        } catch (Exception e) {
            throw new ExpressionEvaluationException(expression, e);
        }
    }

}
