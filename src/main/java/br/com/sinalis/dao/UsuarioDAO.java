package br.com.sinalis.dao;
import br.com.sinalis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
}
