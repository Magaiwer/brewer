package com.algaworks.brewer.session;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

class TabelaItensVenda {

    private String uuid;
    private List<ItemVenda> itens = new ArrayList<>();

    public TabelaItensVenda(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getValorTotal() {
        return itens.stream()
                .map(ItemVenda::getValorTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public void adicionarItem(Cerveja cerveja, Integer quantidadee) {
        Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);

        ItemVenda itemVenda = null;
        if (itemVendaOptional.isPresent()) {
            itemVenda = itemVendaOptional.get();
            itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidadee);
        } else {
            itemVenda = new ItemVenda();
            itemVenda.setCerveja(cerveja);
            itemVenda.setQuantidade(quantidadee);
            itemVenda.setValorUnitario(cerveja.getValor());
            itens.add(0, itemVenda);
        }
    }

    public void excluirItem(Cerveja cerveja) {
        int indice = IntStream.range(0,itens.size())
                .filter(i ->  itens.get(i).getCerveja().equals(cerveja))
                .findAny().getAsInt();
        itens.remove(indice);
    }

    public void alterarQuantidadeItens(Cerveja cerveja,Integer quantidade){
        ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
        itemVenda.setQuantidade(quantidade);
    }

    public int total() {
        return itens.size();
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
        return itens.stream()
                .filter(i -> i.getCerveja().equals(cerveja))
                .findAny();
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabelaItensVenda)) return false;
        TabelaItensVenda that = (TabelaItensVenda) o;
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
