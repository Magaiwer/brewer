package com.algaworks.brewer.repository.helper.cerveja;

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;
import com.algaworks.brewer.storage.FotoStorage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CervejasImpl implements CervejasQueries {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PaginacaoUtil paginacaoUtil;

    @Autowired
    private FotoStorage fotoStorage;

    @Override
    @Transactional(readOnly = true)
    public Page<Cerveja> filtrar(CervejaFilter filter, Pageable pageable) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Cerveja.class);

        paginacaoUtil.preparar(criteria, pageable);
        adicionarFiltro(filter, criteria);

        return new PageImpl<>(criteria.list(), pageable, total(filter));
    }


    private void adicionarFiltro(CervejaFilter filter, Criteria criteria) {
        if (filter != null) {

            if (!StringUtils.isEmpty(filter.getSku())) {
                criteria.add(Restrictions.eq("sku", filter.getSku()));
            }

            if (!StringUtils.isEmpty(filter.getNome())) {
                criteria.add(Restrictions.ilike("nome", filter.getNome(), MatchMode.ANYWHERE));
            }

            if (isEstiloPresent(filter)) {
                criteria.add(Restrictions.eq("estilo", filter.getEstilo()));
            }

            if (filter.getSabor() != null) {
                criteria.add(Restrictions.eq("sabor", filter.getSabor()));
            }

            if (filter.getOrigem() != null) {
                criteria.add(Restrictions.eq("origem", filter.getOrigem()));
            }

            if (filter.getValorDe() != null) {
                criteria.add(Restrictions.ge("valor", filter.getValorDe()));
            }
            if (filter.getValorAte() != null) {
                criteria.add(Restrictions.le("valor", filter.getValorAte()));
            }
        }
    }

    private Long total(CervejaFilter filter) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Cerveja.class);
        adicionarFiltro(filter, criteria);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    private boolean isEstiloPresent(CervejaFilter filter) {
        return filter.getEstilo() != null && filter.getEstilo().getCodigo() != null;
    }

    @Override
    public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
        String jpql = "Select new com.algaworks.brewer.dto.CervejaDTO(codigo,sku,nome,origem,valor,foto) " +
                " from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";

        List<CervejaDTO> cervejasFiltradas = entityManager.createQuery(jpql,CervejaDTO.class)
                .setParameter("skuOuNome",skuOuNome + "%").getResultList();
        cervejasFiltradas.forEach(c-> c.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));

        return cervejasFiltradas;
    }
}
