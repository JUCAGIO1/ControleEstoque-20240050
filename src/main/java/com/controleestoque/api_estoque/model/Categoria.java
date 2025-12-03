package com.controleestoque.api_estoque.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_categoria")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Produto> produtos;

    // Getters and Setters

    public Categoria() {}

    public Categoria(String nome, List<Produto> produtos) {
        this.nome = nome;
        this.produtos = produtos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<produto> getProdutos() { return produtos; }
    public void setProdutos(List<produto> produtos) { this.produtos = produtos; }
}
