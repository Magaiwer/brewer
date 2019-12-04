package com.algaworks.brewer.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FotoStorage {

    String THUMBNAIL_PREFIX = "thumbnail.";
    String URL = "http://localhost:8080/fotos/";

    String salvar(MultipartFile files[]);

    byte[] recuperarFoto(String nome);

    byte[] recuperarThumbnail(String nome);

    void excluir(String foto);

    String getUrl(String foto);

    default String renomearArquivo(String nomeOriginal) {
        return UUID.randomUUID().toString() + "_" + nomeOriginal;

    }
}
