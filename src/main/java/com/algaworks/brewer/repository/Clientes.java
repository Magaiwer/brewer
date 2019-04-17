package com.algaworks.brewer.repository;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.helper.cliente.ClientesQueries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Clientes extends JpaRepository<Cliente, Long>, ClientesQueries {

    public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj);

    List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
}
