package com.algaworks.brewer.repository.filter;


import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;

import java.math.BigDecimal;

public class CervejaFilter {
    private String sku;
    private String nome;
    private Estilo estilo;
    private Origem origem;
    private Sabor sabor;
    private BigDecimal valorDe;
    private BigDecimal valorAte;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Estilo getEstilo() {
        return estilo;
    }

    public void setEstilo(Estilo estilo) {
        this.estilo = estilo;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public Sabor getSabor() {
        return sabor;
    }

    public void setSabor(Sabor sabor) {
        this.sabor = sabor;
    }

    public BigDecimal getValorDe() {
        return valorDe;
    }

    public void setValorDe(BigDecimal valorDe) {
        this.valorDe = valorDe;
    }

    public BigDecimal getValorAte() {
        return valorAte;
    }

    public void setValorAte(BigDecimal valorAte) {
        this.valorAte = valorAte;
    }
}
