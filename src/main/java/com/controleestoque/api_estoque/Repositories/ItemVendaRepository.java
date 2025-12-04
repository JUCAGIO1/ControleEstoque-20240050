package com.controleestoque.api_estoque.Repositories;

import com.controleestoque.api_estoque.Entitys.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    boolean existsByProdutoId(Long produtoId);
}

