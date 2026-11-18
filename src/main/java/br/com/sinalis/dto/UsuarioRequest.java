package br.com.sinalis.dto;
import br.com.sinalis.model.Usuario;
import jakarta.validation.constraints.*;
public record UsuarioRequest(@NotBlank @Size(max = 100) String nome,
                             @NotBlank @Email String email,
                             @NotNull Usuario.Perfil perfil) {}
