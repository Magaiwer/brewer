package com.algaworks.brewer.controller.validator;

import com.algaworks.brewer.model.Venda;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Component
public class VendaValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Venda.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors,"cliente.codigo","",
                "Selecione um cliente na pesquisa rápida!");

        Venda venda = (Venda) obj;

        validarValorTotalNegativo(errors, venda);
        validarSeInformouItemNaVenda(errors, venda);
        validarSeinformouApenasDataEntrega(errors, venda);

    }

    private void validarSeinformouApenasDataEntrega(Errors errors, Venda venda) {
        if(venda.getHorarioEntrega() != null && venda.getDataEntrega() == null){
            errors.reject("","Informe tambem a data de entrega");
        }
    }

    private void validarSeInformouItemNaVenda(Errors errors, Venda venda) {
        if(venda.getItens().isEmpty()){
            errors.reject("","Adicione pelo menos um item a venda");
        }
    }

    private void validarValorTotalNegativo(Errors errors, Venda venda) {
        if(venda.getValorTotal() .compareTo(BigDecimal.ZERO) < 0){
            errors.reject("","Valor Total não pode ser negativo!");
        }
    }
}
