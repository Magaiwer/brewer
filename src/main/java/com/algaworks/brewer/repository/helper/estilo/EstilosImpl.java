package com.algaworks.brewer.repository.helper.estilo;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EstilosImpl implements EstilosQueries {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PaginacaoUtil paginacaoUtil;


    @Override
    @Transactional(readOnly = true)
    public Page<Estilo> filtrar(EstiloFilter estiloFilter, Pageable pageable) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Estilo.class);

        paginacaoUtil.preparar(criteria, pageable);
        adicionarFiltro(estiloFilter, criteria);

        return new PageImpl<Estilo>(criteria.list(), pageable, total(estiloFilter));
    }

    private long total(EstiloFilter estiloFilter) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Estilo.class);
        adicionarFiltro(estiloFilter, criteria);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    private void adicionarFiltro(EstiloFilter estiloFilter, Criteria criteria) {

        if (estiloFilter != null) {
            if (!StringUtils.isEmpty(estiloFilter.getNome())) {
                criteria.add(Restrictions.ilike("nome", estiloFilter.getNome(), MatchMode.ANYWHERE));
            }
        }
    }
}
