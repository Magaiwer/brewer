package com.algaworks.brewer.repository.filter;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.TipoPessoa;

import java.util.List;

public class UsuarioFilter {

    private String nome;
    private String email;
    private List<Grupo> grupos;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }
}
