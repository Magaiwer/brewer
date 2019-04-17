package com.algaworks.brewer.repository.helper.usuario;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuariosImpl implements UsuariosQueries {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaginacaoUtil paginacaoUtil;

    @Override
    public Optional<Usuario> porEmailEAtivo(String email) {
        return entityManager
                .createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
                .setParameter("email", email).getResultList().stream().findFirst();
    }

    @Override
    public List<String> permissoes(Usuario usuario) {
        return entityManager
                .createQuery("select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
                .setParameter("usuario", usuario).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> filter(UsuarioFilter filter, Pageable pageable) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Usuario.class);

        paginacaoUtil.preparar(criteria, pageable);
        adicionarFiltro(criteria, filter);

        List<Usuario> filtrados = criteria.list();
        filtrados.forEach(u -> Hibernate.initialize(u.getGrupos()));

        return new PageImpl<>(criteria.list(), pageable, total(filter));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioComGrupos(Long codigo) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Usuario.class);
        criteria.createAlias("grupos","g",JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("codigo",codigo));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (Usuario) criteria.uniqueResult();
    }

    private void adicionarFiltro(Criteria criteria, UsuarioFilter filter) {
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getNome())) {
                criteria.add(Restrictions.ilike("nome", filter.getNome(), MatchMode.ANYWHERE));
            }
            if (!StringUtils.isEmpty(filter.getEmail())) {
                criteria.add(Restrictions.ilike("email", filter.getEmail(), MatchMode.START));
            }

            if (filter.getGrupos() != null && !filter.getGrupos().isEmpty()) {
                List<Criterion> subQueries = new ArrayList<>();

                for (Long codigoGrupo : filter.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
                    DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UsuarioGrupo.class);

                    detachedCriteria.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
                    detachedCriteria.setProjection(Projections.property("id.usuario"));

                    subQueries.add(Subqueries.propertyIn("codigo", detachedCriteria));
                }

                Criterion[] criterions = new Criterion[subQueries.size()];
                criteria.add(Restrictions.and(subQueries.toArray(criterions)));
            }
        }
    }

    private Long total(UsuarioFilter filter) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Usuario.class);
        adicionarFiltro(criteria, filter);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }
}

