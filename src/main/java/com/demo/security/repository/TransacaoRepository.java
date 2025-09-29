package com.demo.security.repository;

import com.demo.security.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaOrigemId(Long contaId);

    // VULNERABILIDADE: SQL Injection em busca de transações
    @Query(value = "SELECT * FROM transacoes WHERE tipo = ?1 ORDER BY data_hora DESC", nativeQuery = true)
    List<Transacao> findByTipoVulneravel(String tipo);

    // Método seguro
    @Query("SELECT t FROM Transacao t WHERE t.tipo = :tipo ORDER BY t.dataHora DESC")
    List<Transacao> findByTipoSeguro(@Param("tipo") String tipo);
}