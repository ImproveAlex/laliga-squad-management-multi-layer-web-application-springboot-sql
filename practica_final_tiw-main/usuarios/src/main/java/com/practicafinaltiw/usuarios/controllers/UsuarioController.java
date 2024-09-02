package com.practicafinaltiw.usuarios.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.practicafinaltiw.usuarios.domains.Usuario;
import com.practicafinaltiw.usuarios.repositories.UsuarioRepository;

@Controller
@CrossOrigin
public class UsuarioController {
	@Autowired
	UsuarioRepository Us;

	@GetMapping("/usuarios")
	public @ResponseBody List<Usuario> getUsers() {
		return Us.findAll();
	}

	@GetMapping("/usuarios/{correo}")
	public @ResponseBody Usuario getCorreo(@PathVariable String correo) {
		return Us.findByCorreo(correo);
	}

	@PostMapping("/usuarios")
	public ResponseEntity<?> saveUser(@RequestBody @Validated Usuario puser) {
	    Usuario existingUser = Us.findByCorreo(puser.getCorreo());
	    if (existingUser != null) {
	        return ResponseEntity.badRequest().body("El correo ya está registrado");
	    } else {
	        Usuario savedUser = Us.save(puser);
	        return ResponseEntity.ok(savedUser);
	    }
	}

	@DeleteMapping("/usuarios")
	public @ResponseBody void deleteUser(@RequestParam String correo) {
		Usuario usuario = Us.findByCorreo(correo);
		if (usuario != null) {
			Us.delete(usuario);
		}
	}

	@PutMapping("/usuarios")
	public @ResponseBody Usuario updateUser(@RequestBody Usuario pUser) {
		Usuario us = Us.findById(pUser.getCorreo()).orElse(null);
		us.setNombre(pUser.getNombre());
		us.setApellidos(pUser.getApellidos());
		us.setRol(pUser.getRol());
		us.setContrasena(pUser.getContrasena());
		us.setEquipo(pUser.getEquipo());
		return Us.save(us);
	}
}
