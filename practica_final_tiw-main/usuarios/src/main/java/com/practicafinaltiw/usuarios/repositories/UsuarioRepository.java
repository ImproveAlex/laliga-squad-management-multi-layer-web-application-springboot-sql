package com.practicafinaltiw.usuarios.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.practicafinaltiw.usuarios.domains.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, String> {
	public List<Usuario> findAll();

	public Usuario findByCorreo(String correo);
}