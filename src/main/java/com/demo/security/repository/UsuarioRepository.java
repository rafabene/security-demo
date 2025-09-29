package com.demo.security.repository;

import com.demo.security.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpf(String cpf);

    // VULNERABILIDADE: SQL Injection na busca por usuário
    @Query(value = "SELECT * FROM usuarios WHERE cpf = ?1 AND senha = ?2", nativeQuery = true)
    List<Usuario> loginVulneravel(String cpf, String senha);

    // Método seguro para comparação
    @Query("SELECT u FROM Usuario u WHERE u.cpf = :cpf AND u.senha = :senha")
    Optional<Usuario> loginSeguro(@Param("cpf") String cpf, @Param("senha") String senha);
}