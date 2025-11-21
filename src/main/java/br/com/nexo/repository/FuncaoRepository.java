package br.com.nexo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.nexo.model.Funcao;

@Repository
public interface FuncaoRepository extends JpaRepository<Funcao, Long> {
}
