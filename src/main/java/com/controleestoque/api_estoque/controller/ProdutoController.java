package com.controleestoque.api_estoque.controller;

import com.controleestoque.api_estoque.model.Produto;
import com.controleestoque.api_estoque.model.Fornecedor;
import com.controleestoque.api_estoque.repository.ProdutoRepository;
import com.controleestoque.api_estoque.repository.CategoriaRepository;
import com.controleestoque.api_estoque.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    // GET /api/produtos
    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    // GET /api/produtos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/produtos
    // Neste método, assumimos que a Categoria e os Fornecedores já existem.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        
        // 1. Gerenciamento do 1:N (Categoria)
        // A categoria deve ser buscada para garantir que existe e estar no contexto de persistência.
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build(); // Categoria é obrigatória
        }
        
        categoriaRepository.findById(produto.getCategoria().getId())
            .ifPresent(produto::setCategoria); // Associa a categoria gerenciada

        // 2. Gerenciamento do N:M (Fornecedores)
        // Busca todos os fornecedores pelos IDs fornecidos
        if (produto.getFornecedores() != null && !produto.getFornecedores().isEmpty()) {
            
            Set<Fornecedor> fornecedoresGerenciados = new HashSet<>();
            
            // Itera sobre os fornecedores enviados para buscar os reais no banco
            produto.getFornecedores().forEach(fornecedor -> {
                if(fornecedor.getId() != null) {
                   fornecedorRepository.findById(fornecedor.getId())
                       .ifPresent(fornecedoresGerenciados::add); // Adiciona o Fornecedor gerenciado
                }
            });
            
            // Substitui a lista recebida pela lista validada do banco
            produto.setFornecedores(fornecedoresGerenciados);
        }

        // 3. Salva o Produto (e o Estoque, se o CASCADE estiver configurado)
        Produto savedProduto = produtoRepository.save(produto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
    }

    // PUT /api/produtos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody Produto produtoDetails) {
        // Tenta encontrar o produto existente
        return produtoRepository.findById(id)
                .map(produto -> {
                    // Atualiza os dados do produto encontrado
                    produto.setNome(produtoDetails.getNome());
                    produto.setPreco(produtoDetails.getPreco());
                    // Nota: Atualizar relacionamentos aqui requereria lógica similar ao POST
                    
                    Produto updatedProduto = produtoRepository.save(produto);
                    return ResponseEntity.ok(updatedProduto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/produtos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        // Tenta encontrar e deletar
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}