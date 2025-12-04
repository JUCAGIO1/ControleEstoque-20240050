package com.controleestoque.api_estoque.Repositories;

import com.controleestoque.api_estoque.Entitys.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

}

