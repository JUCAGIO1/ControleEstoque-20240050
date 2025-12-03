package com.controleestoque.api_estoque.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.controleestoque.api_estoque.model.Fornecedor;
import com.controleestoque.api_estoque.repository.FornecedorRepository;

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

    // GET /api/fornecedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> getFornecedorById(@PathVariable Long id) {
        // Busca o Fornecedor pelo ID.
        return fornecedorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor createFornecedor(@RequestBody Fornecedor fornecedor) {
        return fornecedorRepository.save(fornecedor);
    }

    // PUT /api/fornecedores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> updateFornecedor(
            @PathVariable Long id, @RequestBody Fornecedor fornecedorDetails) {
        // Tenta encontrar o fornecedor existente
        return fornecedorRepository.findById(id)
                .map(fornecedor -> {
                    // Atualiza os dados do fornecedor encontrado
                    fornecedor.setNome(fornecedorDetails.getNome());
                    Fornecedor updatedFornecedor = fornecedorRepository.save(fornecedor);
                    return ResponseEntity.ok(updatedFornecedor);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/fornecedores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFornecedor(@PathVariable Long id) {
        // Tenta encontrar e deletar
        if (!fornecedorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fornecedorRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna código 204 (No Content)
    }
}