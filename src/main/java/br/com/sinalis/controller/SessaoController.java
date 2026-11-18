package br.com.sinalis.controller;
import br.com.sinalis.bo.SessaoBO;
import br.com.sinalis.dto.SessaoRequest;
import br.com.sinalis.model.SessaoComunicacao;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/sessoes")
public class SessaoController {
    private final SessaoBO bo;
    public SessaoController(SessaoBO bo) { this.bo = bo; }
    @GetMapping public List<SessaoComunicacao> listar() { return bo.listar(); }
    @GetMapping("/{id}") public SessaoComunicacao buscar(@PathVariable Long id) { return bo.buscar(id); }
    @PostMapping public ResponseEntity<SessaoComunicacao> criar(@Valid @RequestBody SessaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bo.criar(req.usuarioId()));
    }
    @PutMapping("/{id}/encerrar") public SessaoComunicacao encerrar(@PathVariable Long id) { return bo.encerrar(id); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
