package com.practicafinaltiw.plantilla.controllers;

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
import org.springframework.web.client.RestTemplate;

import com.practicafinaltiw.plantilla.domains.Jugador;
import com.practicafinaltiw.plantilla.repositories.JugadorRepository;

@Controller
@CrossOrigin
public class JugadorController {
    RestTemplate restTemplate;
    @Autowired
    JugadorRepository jugadorRepository;

    @GetMapping("/jugadores")
    public @ResponseBody List<Jugador> findJugadores() {
        return jugadorRepository.findAll();
    }

    @GetMapping("/jugadores/{dni}")
    public @ResponseBody Jugador getDNI(@PathVariable String dni) {
        return jugadorRepository.getJugadorBydni(dni);
    }

    @GetMapping("/jugadores/equipo/{equipo}")
    public @ResponseBody List<Jugador> getEquipo(@PathVariable String equipo) {
        return jugadorRepository.getJugadorsByEquipo(equipo);
    }

    @PostMapping("/jugadores")
    public ResponseEntity<?> saveJugador(@RequestBody @Validated Jugador jugador) {
        // Check if the maximum limit is reached for the given position in the team
        jugador.setPosicion(jugador.getPosicion().toLowerCase());
        if (!seAlcanzoLimitePosicion(jugador.getEquipo(), jugador.getPosicion())) {
            Jugador existingJugador = jugadorRepository.getJugadorBydni(jugador.getDni());
            if (existingJugador != null) {
                return ResponseEntity.badRequest().body("El Jugador ya está registrado");
            } else {
                Jugador savedJugador = jugadorRepository.save(jugador);
                return ResponseEntity.ok(savedJugador);
            }
        } else {
            return ResponseEntity.badRequest()
                    .body("El equipo ya tiene el número máximo de jugadores para esa posición");
        }
    }

    @DeleteMapping("/jugadores")
    public @ResponseBody void deleteJugador(@RequestParam String dni) {
        Jugador jugador = jugadorRepository.getJugadorBydni(dni);
        if (jugador != null) {
            jugadorRepository.delete(jugador);
        }
    }

    @PutMapping("/jugadores")
    public @ResponseBody Jugador updateJugador(@RequestBody Jugador jugador) {
        Jugador us = jugadorRepository.findById(jugador.getDni()).orElse(null);
        us.setNombre(jugador.getNombre());
        us.setApellidos(jugador.getApellidos());
        us.setAlias(jugador.getAlias());
        us.setPosicion(jugador.getPosicion());
        us.setEquipo(jugador.getEquipo());
        us.setRutaFoto(jugador.getRutaFoto());
        return jugadorRepository.save(us);
    }

    private boolean seAlcanzoLimitePosicion(String equipo, String posicion) {
        List<Jugador> jugadores = jugadorRepository.getJugadorsByEquipo(equipo);

        // Count the number of players for the given position
        long count = jugadores.stream()
                .filter(jugador -> jugador.getPosicion().equals(posicion))
                .count();
        // Verificar el límite máximo según la posición
        switch (posicion) {
            case "portero":
                return count >= 3;
            case "defensa":
                return count >= 8;
            case "medio":
                return count >= 8;
            case "delantero":
                return count >= 6;
            default:
                return false; // Unknown position
        }
    }

}
