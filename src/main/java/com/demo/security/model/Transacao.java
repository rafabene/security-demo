package com.demo.security.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // DEPOSITO, SAQUE, TRANSFERENCIA

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id")
    private ContaBancaria contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    private ContaBancaria contaDestino;

    private String descricao;

    public Transacao() {
        this.dataHora = LocalDateTime.now();
    }

    public Transacao(String tipo, BigDecimal valor, ContaBancaria contaOrigem, ContaBancaria contaDestino, String descricao) {
        this.tipo = tipo;
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public ContaBancaria getContaOrigem() { return contaOrigem; }
    public void setContaOrigem(ContaBancaria contaOrigem) { this.contaOrigem = contaOrigem; }

    public ContaBancaria getContaDestino() { return contaDestino; }
    public void setContaDestino(ContaBancaria contaDestino) { this.contaDestino = contaDestino; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}