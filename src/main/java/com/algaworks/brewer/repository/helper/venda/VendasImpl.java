package com.algaworks.brewer.repository.helper.venda;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
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
import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VendasImpl implements VendasQueries {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaginacaoUtil paginacaoUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<Venda> filtrar(VendaFilter vendaFilter, Pageable pageable) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Venda.class);

        paginacaoUtil.preparar(criteria, pageable);

        adicionarFiltro(vendaFilter, criteria);

        return new PageImpl<>(criteria.list(), pageable, total(vendaFilter));
    }

    @Override
    @Transactional(readOnly = true)
    public Venda buscarVendaComItens(Long codigo) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Venda.class);
        criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("codigo", codigo));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (Venda) criteria.uniqueResult();
    }

    @Override
    public BigDecimal valorTotalNoAno() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                entityManager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
                        .setParameter("ano", Year.now().getValue())
                        .setParameter("status", StatusVenda.EMITIDA)
                        .getSingleResult());

        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal valorTotalNoMes() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                entityManager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
                        .setParameter("mes", MonthDay.now().getMonthValue())
                        .setParameter("status", StatusVenda.EMITIDA)
                        .getSingleResult());

        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal valorTicketMedioNoAno() {
        Optional<BigDecimal> optional = Optional.ofNullable(
                entityManager.createQuery("select sum(valorTotal) / count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
                        .setParameter("ano", Year.now().getValue())
                        .setParameter("status", StatusVenda.EMITIDA)
                        .getSingleResult());

        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public List<VendaMes> totalPorMes() {
        List<VendaMes> vendasMes = entityManager.createNamedQuery("Vendas.totalPorMes").getResultList();

        LocalDate hoje = LocalDate.now();
        for (int i = 1; i <= 6; i++) {
            String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());

            boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
            if (!possuiMes) {
                vendasMes.add(i - 1, new VendaMes(mesIdeal, 0));
            }

            hoje = hoje.minusMonths(1);
        }
        Collections.sort(vendasMes, (VendaMes v1, VendaMes v2) -> v2.getMes().compareTo(v1.getMes()));
        return vendasMes;
    }

    @Override
    public List<VendaOrigem> totalPorOrigem() {
        List<VendaOrigem> vendasOrigem = entityManager.createNamedQuery("Vendas.porOrigem").getResultList();

        LocalDate hoje = LocalDate.now();
        for (int i = 1; i <= 6; i++) {
            String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());

            boolean possuiMes = vendasOrigem.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
            if (!possuiMes) {
                vendasOrigem.add(i - 1, new VendaOrigem(mesIdeal, 0,0));
            }

            hoje = hoje.minusMonths(1);
        }
        Collections.sort(vendasOrigem, (VendaOrigem v1, VendaOrigem v2) -> v2.getMes().compareTo(v1.getMes()));
        return vendasOrigem;
    }


    private Long total(VendaFilter vendaFilter) {
        Criteria criteria = entityManager.unwrap(Session.class).createCriteria(Venda.class);
        adicionarFiltro(vendaFilter, criteria);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    private void adicionarFiltro(VendaFilter vendaFilter, Criteria criteria) {
        criteria.createAlias("cliente", "c");

        if (vendaFilter != null) {
            if (!StringUtils.isEmpty(vendaFilter.getCodigo())) {
                criteria.add(Restrictions.eq("codigo", vendaFilter.getCodigo()));
            }

            if (vendaFilter.getStatusVenda() != null) {
                criteria.add(Restrictions.eq("status", vendaFilter.getStatusVenda()));
            }

            if (vendaFilter.getDesde() != null) {
                LocalDateTime desde = LocalDateTime.of(vendaFilter.getDesde(), LocalTime.of(0, 0));
                criteria.add(Restrictions.ge("dataCriacao", desde));
            }

            if (vendaFilter.getAte() != null) {
                LocalDateTime ate = LocalDateTime.of(vendaFilter.getAte(), LocalTime.of(0, 0));
                criteria.add(Restrictions.le("dataCriacao", ate));
            }

            if (vendaFilter.getValorMinimo() != null) {
                criteria.add(Restrictions.ge("valorTotal", vendaFilter.getValorMinimo()));
            }

            if (vendaFilter.getValorMaximo() != null) {
                criteria.add(Restrictions.le("valorTotal", vendaFilter.getValorMaximo()));
            }

            if (!StringUtils.isEmpty(vendaFilter.getNomeCliente())) {
                criteria.add(Restrictions.ilike("c.nome", vendaFilter.getNomeCliente(), MatchMode.ANYWHERE));
            }

            if (!StringUtils.isEmpty(vendaFilter.getCpfOuCnpjCliente())) {
                criteria.add(Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(vendaFilter.getCpfOuCnpjCliente())));
            }
        }

    }
}
