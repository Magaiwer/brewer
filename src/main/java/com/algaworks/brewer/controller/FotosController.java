package com.algaworks.brewer.controller;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorageRunnable;
import com.algaworks.brewer.storage.FotoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/fotos")
public class FotosController {

    @Autowired
    private FotoStorage fotoStorage;

    @PostMapping
    public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile [] files){
        DeferredResult<FotoDTO> resultado = new DeferredResult<>();
        Thread thread = new Thread(new FotoStorageRunnable(files,resultado,fotoStorage));
        thread.start();
        return resultado;

    }

    @GetMapping("/{nome:.*}")
    public byte[] recuperarFoto(@PathVariable String nome){
        return fotoStorage.recuperarFoto(nome);

    }
}
