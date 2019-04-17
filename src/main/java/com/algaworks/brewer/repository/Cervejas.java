package com.algaworks.brewer.repository;

import com.algaworks.brewer.dto.ValorItensEstoque;
import com.algaworks.brewer.repository.helper.cerveja.CervejasQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;

@Repository
public interface Cervejas extends JpaRepository<Cerveja, Long>, CervejasQueries {

    @Query(value = "select new com.algaworks.brewer.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Cerveja")
    ValorItensEstoque valorItensEstoque();

}
