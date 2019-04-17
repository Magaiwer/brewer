package com.algaworks.brewer.service;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.service.exception.CidadeJaCadastradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CadastroCidadeService {

    @Autowired
    private Cidades cidades;

    @Transactional
    public void salvar(Cidade cidade) {
        Optional<Cidade> cidadeExistente = cidades.findByNomeAndEstado(cidade.getNome(),cidade.getEstado());

        if (cidadeExistente.isPresent()) {
            throw new CidadeJaCadastradoException("Cidade já cadastrado");
        }
        cidades.save(cidade);
    }

}
