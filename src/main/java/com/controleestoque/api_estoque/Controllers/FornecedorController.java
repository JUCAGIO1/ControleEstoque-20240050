package com.controleestoque.api_estoque.Controllers;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.controleestoque.api_estoque.Entitys.Fornecedor;
import com.controleestoque.api_estoque.Repositories.FornecedorRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {
    private final FornecedorRepository fornecedorRepository;

    @GetMapping
    public List<Fornecedor> getAllFornecedores() {
        return fornecedorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> getCategoriaById(@PathVariable Long id) {
        return fornecedorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor createFornecedor(@RequestBody Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> updateFornecedor(@PathVariable Long id, @RequestBody Fornecedor fornecedorDetails){
        return fornecedorRepository.findById(id).map(fornecedor -> {
            fornecedor.setNome(fornecedorDetails.getNome());
            Fornecedor updatedFornecedor = fornecedorRepository.save(fornecedor);
            return ResponseEntity.ok(updatedFornecedor);
        })
        .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFornecedor(@PathVariable Long id){
        return fornecedorRepository.findById(id).map(fornecedor -> {

        boolean hasProducts = fornecedor.getProdutos() != null && !fornecedor.getProdutos().isEmpty();

        if (hasProducts) {

            fornecedor.setAtivo(false);
            fornecedorRepository.save(fornecedor);
        } else {

            fornecedorRepository.delete(fornecedor);
        }

        return ResponseEntity.noContent().build();

        }).orElse(ResponseEntity.notFound().build());
    }
}

