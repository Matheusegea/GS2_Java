package br.com.sinalis.controller;
import br.com.sinalis.bo.UsuarioBO;
import br.com.sinalis.dto.UsuarioRequest;
import br.com.sinalis.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioBO bo;
    public UsuarioController(UsuarioBO bo) { this.bo = bo; }
    @GetMapping public List<Usuario> listar() { return bo.listar(); }
    @GetMapping("/{id}") public Usuario buscar(@PathVariable Long id) { return bo.buscar(id); }
    @PostMapping public ResponseEntity<Usuario> criar(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bo.criar(req));
    }
    @PutMapping("/{id}") public Usuario atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req) {
        return bo.atualizar(id, req);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
