package com.algaworks.brewer.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuario_grupo")
public class UsuarioGrupo implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UsarioGrupoId id;

    public UsarioGrupoId getId() {
        return id;
    }

    public void setId(UsarioGrupoId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioGrupo)) return false;
        UsuarioGrupo that = (UsuarioGrupo) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
