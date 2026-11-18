package br.com.sinalis.controller;
import br.com.sinalis.bo.TraducaoBO;
import br.com.sinalis.dto.TraducaoRequest;
import br.com.sinalis.model.Traducao;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/traducoes")
public class TraducaoController {
    private final TraducaoBO bo;
    public TraducaoController(TraducaoBO bo) { this.bo = bo; }
    @GetMapping public List<Traducao> listar(@RequestParam(required = false) Long sessaoId) { return bo.listar(sessaoId); }
    @GetMapping("/{id}") public Traducao buscar(@PathVariable Long id) { return bo.buscar(id); }
    @PostMapping public ResponseEntity<Traducao> criar(@Valid @RequestBody TraducaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bo.criar(req));
    }
    @PutMapping("/{id}") public Traducao atualizar(@PathVariable Long id, @Valid @RequestBody TraducaoRequest req) {
        return bo.atualizar(id, req);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
