package com.algaworks.brewer.storage.local;

import com.algaworks.brewer.storage.FotoStorage;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.FileSystems.getDefault;

@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {

    public static final Logger LOGGER = LoggerFactory.getLogger(FotoStorageLocal.class);
    private static final String THUMBNAIL_PREFIX = "thumbnail.";


    private Path local;

    public FotoStorageLocal() {
        this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
    }

    public FotoStorageLocal(Path path) {
        this.local = path;
        criarPastas();
    }


    @Override
    public String salvar(MultipartFile[] files) {
        String novoNome = null;
        if (files != null && files.length > 0) {
            MultipartFile arquivo = files[0];
            novoNome = renomearArquivo(arquivo.getOriginalFilename());
            try {
                arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
            } catch (IOException e) {
                throw new RuntimeException("Erro salvando foto na pasta temporaria", e);
            }
        }

        try {
            Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
        } catch (IOException e) {
            throw new RuntimeException("Erro gerando thumbnail",e);
        }
        return novoNome;
    }

    @Override
    public void excluir(String foto) {
        try {
            Files.deleteIfExists(this.local.resolve(foto));
            Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
        } catch (IOException e) {
            LOGGER.warn(String.format("Erro apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));
        }

    }

    @Override
    public String getUrl(String foto) {
        return "http://localhost:8080/brewer/fotos/" + foto;
    }

    @Override
    public byte[] recuperarFoto(String nome) {
        try {
            return Files.readAllBytes(this.local.resolve(nome));
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo foto ", e);
        }
    }

    @Override
    public byte[] recuperarThumbnail(String fotoCerveja) {
        return recuperarFoto(THUMBNAIL_PREFIX + fotoCerveja);
    }


    private void criarPastas() {
        try {
            Files.createDirectories(this.local);
            this.local = getDefault().getPath(this.local.toString(), "temp");
            Files.createDirectories(this.local);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Pasta salvar foto criada");
                LOGGER.debug("Pasta default" + this.local.toAbsolutePath());
                LOGGER.debug("Pasta temporaria" + this.local.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro criando pasta salvar foto", e);
        }

    }
}
