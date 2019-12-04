package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;

public class MenuAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private  static String NOME_ATTRIBUTO = "menu";
    private  static  int PRECEDENCIA = 1000;

    public MenuAttributeTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix,null,false,NOME_ATTRIBUTO,true,PRECEDENCIA,true);
    }

    @Override
    protected void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String s, IElementTagStructureHandler iElementTagStructureHandler) {

        IEngineConfiguration configuration = iTemplateContext.getConfiguration();
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        IStandardExpression expression = parser.parseExpression(iTemplateContext, s);
        String menu = (String) expression.execute(iTemplateContext);

        HttpServletRequest request = ((IWebContext) iTemplateContext).getRequest();
        String uri = request.getRequestURI();

        if (uri.matches(menu)) {
            String classesExistentes = iProcessableElementTag.getAttributeValue("class");
            iElementTagStructureHandler.setAttribute("class", classesExistentes + " is-active");
        }
    }
}
