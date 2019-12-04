package com.algaworks.brewer.repository.helper.venda;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface VendasQueries {

    Page<Venda> filtrar(VendaFilter vendaFilter, Pageable pageable);
    Venda buscarVendaComItens(Long codigo);
    BigDecimal valorTotalNoAno();
    BigDecimal valorTotalNoMes();
    BigDecimal valorTicketMedioNoAno();
    List<VendaMes> totalPorMes();
    List<VendaOrigem> totalPorOrigem();
}
