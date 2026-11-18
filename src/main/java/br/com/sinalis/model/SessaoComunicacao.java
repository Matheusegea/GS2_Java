package br.com.sinalis.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "SINALIS_SESSAO")
public class SessaoComunicacao {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessao_seq")
    @SequenceGenerator(name = "sessao_seq", sequenceName = "SINALIS_SESSAO_SEQ", allocationSize = 1)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;
    @Column(nullable = false)
    private LocalDateTime iniciadaEm = LocalDateTime.now();
    private LocalDateTime encerradaEm;
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getIniciadaEm() { return iniciadaEm; }
    public LocalDateTime getEncerradaEm() { return encerradaEm; }
    public void encerrar() { this.encerradaEm = LocalDateTime.now(); }
}
