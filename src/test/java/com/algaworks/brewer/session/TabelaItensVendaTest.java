package com.algaworks.brewer.session;


import com.algaworks.brewer.model.Cerveja;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TabelaItensVendaTest {

    private TabelaItensVenda tabelaItens;

    @Before
    public void setUp(){
        this.tabelaItens = new TabelaItensVenda("1");
    }

    @Test
    public void deveCalcularValorTotalSemItens() {
        assertEquals(BigDecimal.ZERO,tabelaItens.getValorTotal());
    }

    @Test
    public void deveCalcularValorTotalComUmItem() {
        Cerveja cerveja = new Cerveja();
        BigDecimal valor = new BigDecimal("8.90");
        cerveja.setValor(valor);

        tabelaItens.adicionarItem(cerveja,1);

        assertEquals(valor,tabelaItens.getValorTotal());
    }

    @Test
    public void deveCalcularValorTotalComVariosIten() {
        Cerveja cerveja1 = new Cerveja();
        BigDecimal valor1 = new BigDecimal("8.90");
        cerveja1.setCodigo(1L);
        cerveja1.setValor(valor1);

        Cerveja cerveja2 = new Cerveja();
        BigDecimal valor2 = new BigDecimal("4.99");
        cerveja2.setCodigo(2L);
        cerveja2.setValor(valor2);

       tabelaItens .adicionarItem(cerveja1,1);
        tabelaItens.adicionarItem(cerveja2,2);

        assertEquals(new BigDecimal("18.88"),tabelaItens.getValorTotal());
    }

    @Test
    public void deveManterTamanhoDaListaParaMesmaCerveja() {
        Cerveja cerveja = new Cerveja();
        cerveja.setCodigo(1L);

        cerveja.setValor(new BigDecimal("4.50"));

        tabelaItens.adicionarItem(cerveja,1);
        tabelaItens.adicionarItem(cerveja,1);

        assertEquals(1,tabelaItens.total());
        assertEquals(new BigDecimal("9.00"),tabelaItens.getValorTotal());
    }

    @Test
    public void deveAlterarQuantidadeDoItem() {
        Cerveja cerveja = new Cerveja();

        cerveja.setCodigo(1L);
        cerveja.setValor(new BigDecimal("4.50"));

        tabelaItens.adicionarItem(cerveja,1);
        tabelaItens.alterarQuantidadeItens(cerveja,3);

        assertEquals(new BigDecimal("13.50"),tabelaItens.getValorTotal());
    }

    @Test
    public void deveExcluirItem() {
        Cerveja cerveja1 = new Cerveja();
        cerveja1.setCodigo(1L);
        cerveja1.setValor(new BigDecimal("8.90"));

        Cerveja cerveja2 = new Cerveja();
        cerveja2.setCodigo(2L);
        cerveja2.setValor(new BigDecimal("4.99"));

        Cerveja cerveja3 = new Cerveja();
        cerveja3.setCodigo(3L);
        cerveja3.setValor(new BigDecimal("2.00"));


        tabelaItens.adicionarItem(cerveja1,1);
        tabelaItens.adicionarItem(cerveja2,2);
        tabelaItens.adicionarItem(cerveja3,1);

        tabelaItens.excluirItem(cerveja2);

        assertEquals(2,tabelaItens.total());
        assertEquals(new BigDecimal("10.90"),tabelaItens.getValorTotal());
    }
}
