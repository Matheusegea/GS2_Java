package br.com.sinalis.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity
@Table(name = "SINALIS_USUARIO")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "SINALIS_USUARIO_SEQ", allocationSize = 1)
    private Long id;
    @NotBlank @Size(max = 100) @Column(nullable = false, length = 100)
    private String nome;
    @NotBlank @Email @Size(max = 150) @Column(nullable = false, unique = true, length = 150)
    private String email;
    @NotNull @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Perfil perfil;
    public enum Perfil { SURDO, OUVINTE, OUTRO }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}
