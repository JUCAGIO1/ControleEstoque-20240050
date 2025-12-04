package com.controleestoque.api_estoque.Controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.controleestoque.api_estoque.Entitys.Fornecedor;
import com.controleestoque.api_estoque.Entitys.Produto;
import com.controleestoque.api_estoque.Repositories.ProdutoRepository;

import lombok.RequiredArgsConstructor;

import com.controleestoque.api_estoque.Repositories.CategoriaRepository;
import com.controleestoque.api_estoque.Repositories.FornecedorRepository;
import com.controleestoque.api_estoque.Repositories.ItemVendaRepository;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItemVendaRepository itemVendaRepository;

    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getCategoriaById(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {

        if(produto.getCategoria() == null || produto.getCategoria().getId() == null){
            return ResponseEntity.badRequest().build(); 
        }

        categoriaRepository.findById(produto.getCategoria().getId()).ifPresent(produto::setCategoria); 

        Set<Fornecedor> fornecedoresAdicionados = new HashSet<>();
        if (produto.getFornecedores() != null && !produto.getFornecedores().isEmpty()) {

            for (Fornecedor fornecedorReq : produto.getFornecedores()) {
                if (fornecedorReq != null && fornecedorReq.getId() != null) {
                    fornecedorRepository.findById(fornecedorReq.getId())

                        .ifPresent(fornecedoresAdicionados::add);
                }
            }
        }

        produto.setFornecedores(fornecedoresAdicionados);

        if (produto.getEstoque() != null) {
            produto.getEstoque().setProduto(produto); 
        }

        Produto savedProduto = produtoRepository.save(produto);

        for (Fornecedor f : savedProduto.getFornecedores()) {
            f.getProdutos().add(savedProduto);
            fornecedorRepository.save(f);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody Produto produtoDetails) {
        return produtoRepository.findById(id).map(produto -> {

            produto.setNome(produtoDetails.getNome());
            produto.setPreco(produtoDetails.getPreco());

            if (produtoDetails.getCategoria() != null && produtoDetails.getCategoria().getId() != null) {
                categoriaRepository.findById(produtoDetails.getCategoria().getId()).ifPresent(produto::setCategoria);
            }

            if(produto.getFornecedores() != null) {
                produto.getFornecedores().forEach(f -> f.removeProduto(produto));
                produto.getFornecedores().clear();
            }

            for (var fornecedorReq : produtoDetails.getFornecedores()) {
                if (fornecedorReq != null && fornecedorReq.getId() != null) {
                    fornecedorRepository.findById(fornecedorReq.getId()).ifPresent(fornecedorGerenciado -> {
                        fornecedorGerenciado.addProduto(produto);
                    });
                }
            }     

            if (produtoDetails.getEstoque() != null) {
                produto.getEstoque().setQuantidade(produtoDetails.getEstoque().getQuantidade());
            }

            Produto updatedProduto = produtoRepository.save(produto);

            for (Fornecedor f : produto.getFornecedores()) {
                fornecedorRepository.save(f); 
            }
            return ResponseEntity.ok(updatedProduto);

        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduto(@PathVariable Long id) {

        return produtoRepository.findById(id).map(produto -> {
            boolean isProdutoSold = itemVendaRepository.existsByProdutoId(id);

            if(isProdutoSold) {

                produto.setAtivo(false);
                produtoRepository.save(produto);
                return ResponseEntity.noContent().build();
            } else {

                produtoRepository.delete(produto);
                return ResponseEntity.noContent().build();
            }
        })
        .orElse(ResponseEntity.notFound().build());
    }
}

