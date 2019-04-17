package com.algaworks.brewer.service;

import com.algaworks.brewer.service.event.CervejaSalvaEvent;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;

import javax.persistence.PersistenceException;

@Service
public class CadastroCervejaService {

    @Autowired
    private Cervejas cervejas;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private FotoStorage fotoStorage;

    @Transactional
    public void salvar(Cerveja cerveja) {
        cervejas.save(cerveja);
        publisher.publishEvent(new CervejaSalvaEvent(cerveja));
    }


    @Transactional
    public void excluir(Cerveja cerveja) {
        try {
            String foto = cerveja.getFoto();
            cervejas.delete(cerveja);
            cervejas.flush();
            fotoStorage.excluir(foto);
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível excluir a cerveja. Já foi utilizada em uma venda");
        }
    }
}
