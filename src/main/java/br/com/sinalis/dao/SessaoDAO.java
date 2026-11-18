package br.com.sinalis.dao;
import br.com.sinalis.model.SessaoComunicacao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SessaoDAO extends JpaRepository<SessaoComunicacao, Long> {
    boolean existsByUsuarioId(Long usuarioId);
}
