package com.practicafinaltiw.plantilla.repositories;

import org.springframework.data.repository.CrudRepository;
import com.practicafinaltiw.plantilla.domains.Jugador;
import java.util.List;

public interface JugadorRepository extends CrudRepository<Jugador, String> {

    public List<Jugador> findAll();

    public Jugador getJugadorBydni(String dni);

    public List<Jugador> getJugadorsByEquipo(String equipo);
}