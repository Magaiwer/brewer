package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.helper.cidade.CidadesQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.brewer.model.Cidade;
import org.springframework.util.StringUtils;

public interface Cidades extends JpaRepository<Cidade, Long>, CidadesQueries {

	List<Cidade> findByEstadoCodigo(Long codigoEstado);

	Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);

}
