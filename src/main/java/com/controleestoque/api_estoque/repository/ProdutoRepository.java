package com.controleestoque.api_estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.controleestoque.api_estoque.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    // Métodos personalizados podem ser adicionados aqui (ex: findByName)
}