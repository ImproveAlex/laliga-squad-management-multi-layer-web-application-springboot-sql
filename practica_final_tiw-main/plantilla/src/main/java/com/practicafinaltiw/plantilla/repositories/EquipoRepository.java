package com.practicafinaltiw.plantilla.repositories;

import org.springframework.data.repository.CrudRepository;
import com.practicafinaltiw.plantilla.domains.Equipo;
import java.util.List;

public interface EquipoRepository extends CrudRepository<Equipo, String> {

    public List<Equipo> findAll();

    public Equipo getEquipoByNombre(String nombre);
}