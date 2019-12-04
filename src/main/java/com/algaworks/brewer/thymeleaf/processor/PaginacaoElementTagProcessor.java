package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class PaginacaoElementTagProcessor extends AbstractElementTagProcessor {

    private static final String NOME_TAG = "pagination";
    private static final int PRECEDENCIA = 1000;

   public PaginacaoElementTagProcessor(String dialectPrefix){
       super(TemplateMode.HTML,dialectPrefix,NOME_TAG,true,null,false,PRECEDENCIA);

   }

    @Override
    protected void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, IElementTagStructureHandler iElementTagStructureHandler) {
        IModelFactory modelFactory = iTemplateContext.getModelFactory();
        IModel model = modelFactory.createModel();

        IAttribute page = iProcessableElementTag.getAttribute("page");


        model.add(modelFactory.createStandaloneElementTag("th:fragment","th:replace",
                String.format("fragments/Paginacao :: pagination(%s)",page.getValue())
        ));

        iElementTagStructureHandler.replaceWith(model,true);
    }
}
