package com.algaworks.brewer.repository.helper.cliente;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientesQueries {

    public Page<Cliente> filtrar(ClienteFilter clienteFilter, Pageable pageable);
}
