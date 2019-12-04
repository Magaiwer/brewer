package com.algaworks.brewer.storage.s3;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(FotoStorageS3.class);

    private static String BUCKET = "wsbrewer";

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String salvar(MultipartFile[] files) {
        String novoNome = null;

        if (files != null && files.length > 0) {

            MultipartFile arquivo = files[0];
            novoNome = renomearArquivo(arquivo.getOriginalFilename());

            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

            try {
                enviarFoto(novoNome, arquivo, acl);
                enviarThumbnail(novoNome, arquivo, acl);

            } catch (IOException e) {
                throw new RuntimeException("Erro salvando arquivo no S3", e);
            }
        }

        return novoNome;
    }


    @Override
    public byte[] recuperarFoto(String foto) {
        InputStream inputStream = amazonS3.getObject(BUCKET,foto).getObjectContent();

        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            LOGGER.error("Não foi possível recuperar a foto do S3");
        }
        return null;
    }

    @Override
    public byte[] recuperarThumbnail(String foto) {
        return recuperarFoto(THUMBNAIL_PREFIX + foto);
    }

    @Override
    public void excluir(String foto) {
        amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto,THUMBNAIL_PREFIX + foto));
    }

    @Override
    public String getUrl(String foto) {
        if(!StringUtils.isEmpty(foto)){
            return "https://s3-sa-east-1.amazonaws.com/wsbrewer/" + foto;
        }
        return null;
    }

    private void enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(arquivo.getContentType());
        metadata.setContentLength(arquivo.getSize());

        amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
                .withAccessControlList(acl));
    }

    private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(outputStream);
        byte[] array = outputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(array);

        ObjectMetadata thumbMetadata = new ObjectMetadata();
        thumbMetadata.setContentType(arquivo.getContentType());
        thumbMetadata.setContentLength(array.length);

        amazonS3.putObject(new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, inputStream, thumbMetadata)
                .withAccessControlList(acl));
    }
}
