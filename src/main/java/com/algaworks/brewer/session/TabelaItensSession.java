package com.algaworks.brewer.session;


import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class TabelaItensSession {

    private Set<TabelaItensVenda> tabelaItens = new HashSet<>();


    public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
        TabelaItensVenda tabelaItensVenda = buscarTabelaPorId(uuid);
        tabelaItensVenda.adicionarItem(cerveja, quantidade);
        tabelaItens.add(tabelaItensVenda);
    }


    public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
        TabelaItensVenda tabelaItensVenda = buscarTabelaPorId(uuid);
        tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);
    }

    public void excluirItem(String uuid, Cerveja cerveja) {
        buscarTabelaPorId(uuid).excluirItem(cerveja);
    }

    public List<ItemVenda> getItens(String uuid) {
        return buscarTabelaPorId(uuid).getItens();
    }

    public BigDecimal getValorTotal(String uuid) {
        return buscarTabelaPorId(uuid).getValorTotal();
    }

    private TabelaItensVenda buscarTabelaPorId(String uuid) {
        return tabelaItens.stream()
                .filter(t -> t.getUuid().equals(uuid))
                .findAny()
                .orElse(new TabelaItensVenda(uuid));
    }

}
