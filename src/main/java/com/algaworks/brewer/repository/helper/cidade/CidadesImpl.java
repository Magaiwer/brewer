package com.algaworks.brewer.repository.helper.cidade;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CidadesImpl implements CidadesQueries {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaginacaoUtil paginacaoUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<Cidade> filtrar(CidadeFilter cidadeFilter, Pageable pageable) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Cidade.class);
        criteria.createAlias("estado", "e");
        paginacaoUtil.preparar(criteria,pageable);
        adicionarFiltro(cidadeFilter, criteria);

        return new PageImpl<>(criteria.list(),pageable,total(cidadeFilter));
    }

    private void adicionarFiltro(CidadeFilter cidadeFilter, Criteria criteria) {
        if (cidadeFilter != null) {
            if (!StringUtils.isEmpty(cidadeFilter.getNome())) {
                criteria.add(Restrictions.ilike("nome", cidadeFilter.getNome(), MatchMode.ANYWHERE));
            }
            if (cidadeFilter.getEstado() != null) {
                criteria.add(Restrictions.eq("estado", cidadeFilter.getEstado()));
            }
        }
    }

    private Long total(CidadeFilter cidadeFilter) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Cidade.class);
        adicionarFiltro(cidadeFilter, criteria);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }
}
