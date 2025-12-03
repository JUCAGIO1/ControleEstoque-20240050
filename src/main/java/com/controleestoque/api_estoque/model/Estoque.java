package com.exemplo.api.produtos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_estoques")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Integer quantidade;

    // --- Relacionamento 1:1 (One-to-One) ---
    // É o lado "proprietário" (o que contém a chave estrangeira (FK).
    @OneToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonBackReference
    private Produto produto;


    // Construtores, Getters e Setters...
    public Estoque() {}

    public Estoque(Integer quantidade, Produto produto) {
        this.quantidade = quantidade;
        this.produto = produto;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
}