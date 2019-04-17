package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

public class ClassForErrorAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private  static String NOME_ATTRIBUTO = "classforerror";
    private  static  int PRECEDENCIA = 1000;

    public ClassForErrorAttributeTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix,null,false,NOME_ATTRIBUTO,true,PRECEDENCIA,true);
    }

    @Override
    protected void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String s, IElementTagStructureHandler iElementTagStructureHandler) {

        boolean temErro = FieldUtils.hasErrors(iTemplateContext,s);

        if (temErro){
            String classesExistente = iProcessableElementTag.getAttributeValue("class");
            iElementTagStructureHandler.setAttribute("class",classesExistente + " has-error");
        }
    }
}
