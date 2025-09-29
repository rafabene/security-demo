package com.demo.security.repository;

import com.demo.security.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    Optional<ContaBancaria> findByNumeroConta(String numeroConta);

    // VULNERABILIDADE: SQL Injection - Query concatenada diretamente
    @Query(value = "SELECT * FROM contas_bancarias WHERE numero_conta = ?1", nativeQuery = true)
    List<ContaBancaria> findByNumeroContaVulneravel(String numeroConta);

    // Método seguro para comparação
    @Query("SELECT c FROM ContaBancaria c WHERE c.numeroConta = :numeroConta")
    Optional<ContaBancaria> findByNumeroContaSeguro(@Param("numeroConta") String numeroConta);

    // VULNERABILIDADE: Busca por saldo expondo todos os dados
    @Query(value = "SELECT * FROM contas_bancarias WHERE saldo >= ?1", nativeQuery = true)
    List<ContaBancaria> findContasComSaldoMaiorQue(String saldoMinimo);
}