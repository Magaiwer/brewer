package com.algaworks.brewer.repository.listener;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

import javax.persistence.PostLoad;

public class CervejaEntityListener {


    @PostLoad
    public void postLoad(final Cerveja cerveja){
        cerveja.setUrlFoto(FotoStorage.URL + cerveja.getFotoOuMock());
        cerveja.setUrlThumbnailFoto(FotoStorage.URL + FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock());
    }
}
