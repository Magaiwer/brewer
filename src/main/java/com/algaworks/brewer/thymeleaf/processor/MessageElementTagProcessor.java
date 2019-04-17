package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class MessageElementTagProcessor extends AbstractElementTagProcessor {

    private static final String NOME_TAG = "message";
    private static final int PRECEDENCIA = 1000;

   public MessageElementTagProcessor(String dialectPrefix){
       super(TemplateMode.HTML,dialectPrefix,NOME_TAG,true,null,false,PRECEDENCIA);

   }

    @Override
    protected void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, IElementTagStructureHandler iElementTagStructureHandler) {
        IModelFactory modelFactory = iTemplateContext.getModelFactory();
        IModel model = modelFactory.createModel();

        model.add(modelFactory.createStandaloneElementTag("th:fragment","th:replace","fragments/MensagemSucesso :: alert"));
        model.add(modelFactory.createStandaloneElementTag("th:fragment","th:replace","fragments/MensagensErroValidacao :: alert"));

        iElementTagStructureHandler.replaceWith(model,true);
    }
}
