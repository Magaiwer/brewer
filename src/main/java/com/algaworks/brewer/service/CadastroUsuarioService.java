package com.algaworks.brewer.service;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.service.exception.EmailJaCadastradoException;
import com.algaworks.brewer.service.exception.SenhaUsuarioObrigatoriaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CadastroUsuarioService {

    @Autowired
    private Usuarios usuarios;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void salvar(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarios.findByEmail(usuario.getEmail());

        verificarUsuarioExistente(usuario, usuarioExistente);
        validarSenhaObrigatoriaUsuarioNovo(usuario);
        validarSenha(usuario, usuarioExistente);
        validarStatus(usuario, usuarioExistente);

        usuario.setConfirmacaoSenha(usuario.getSenha());
        usuarios.save(usuario);
    }

    private void verificarUsuarioExistente(Usuario usuario, Optional<Usuario> usuarioExistente) {
        if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
            throw new EmailJaCadastradoException("Email já cadastrado");
        }
    }

    private void validarSenhaObrigatoriaUsuarioNovo(Usuario usuario) {
        if (usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())) {
            throw new SenhaUsuarioObrigatoriaException("Senha é obrigatória");
        }
    }

    private void validarSenha(Usuario usuario, Optional<Usuario> usuarioExistente) {
        if (usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else if(StringUtils.isEmpty(usuario.getSenha())) {
            usuario.setSenha(usuarioExistente.get().getSenha());
        }
    }

    private void validarStatus(Usuario usuario, Optional<Usuario> usuarioExistente) {
        if(usuario.isEdit() && usuario.getAtivo() == null) {
            usuario.setAtivo(usuarioExistente.get().getAtivo());
        }
    }

    @Transactional
    public void alterarStatus(Long[] codigos, StatusUsuario statusUsuario) {
        statusUsuario.executar(codigos, usuarios);
    }
}
