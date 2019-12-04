package com.algaworks.brewer.service.event;

import com.algaworks.brewer.model.Cerveja;
import org.springframework.util.StringUtils;

public class CervejaSalvaEvent {

    private Cerveja cerveja;

    public CervejaSalvaEvent(Cerveja cerveja) {
        this.cerveja = cerveja;
    }

    public Cerveja getCerveja() {
        return cerveja;
    }

    public void setCerveja(Cerveja cerveja) {
        this.cerveja = cerveja;
    }

    public boolean temFoto(){
        return !StringUtils.isEmpty(cerveja.getFoto());
    }

    public boolean isNovaFoto(){
        return cerveja.isNovaFoto();
    }

}
