package com.practicafinaltiw.plantilla.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

import com.practicafinaltiw.plantilla.domains.Equipo;
import com.practicafinaltiw.plantilla.repositories.EquipoRepository;
import org.springframework.web.multipart.MultipartFile;

@Controller
@CrossOrigin
public class EquipoController {
    RestTemplate restTemplate;
    @Autowired
    EquipoRepository equipoRepository;

    @GetMapping("/equipos")
    public @ResponseBody List<Equipo> findEquipos() {
        return equipoRepository.findAll();
    }

    @GetMapping("/equipos/{nombre}")
    public @ResponseBody Equipo getEquipo(@PathVariable String nombre) {
        return equipoRepository.getEquipoByNombre(nombre);
    }

    @PostMapping("/equipos")
    public ResponseEntity<?> saveEquipo(@RequestParam String nombre, @RequestParam String foto, // Use MultipartFile to handle file uploads
                                        @RequestParam int posicion) {
        try {
            // Convert MultipartFile to byte[]
                InputStream imageStream = new URL(foto).openStream();
                byte[] fotoBytes = imageStream.readAllBytes();
                Equipo equipo = new Equipo();
                equipo.setNombre(nombre);
                equipo.setFoto(fotoBytes);
                equipo.setPosicion(posicion);

                Equipo existingEquipo = equipoRepository.getEquipoByNombre(equipo.getNombre());
                if (existingEquipo != null) {
                    return ResponseEntity.badRequest().body("El Equipo ya está registrado");
                } else {
                    Equipo savedEquipo = equipoRepository.save(equipo);
                    return ResponseEntity.ok(savedEquipo);
                }
        }
        catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return ResponseEntity.badRequest().body("Error processing the image file");
        }
    }

    @DeleteMapping("/equipos")
    public @ResponseBody void deleteEquipo(@RequestParam String nombre) {
        Equipo equipo = equipoRepository.getEquipoByNombre(nombre);
        if (equipo != null) {
            equipoRepository.delete(equipo);
        }
    }

    @PutMapping("/equipos")
    public @ResponseBody Equipo updateEquipo(@RequestParam String nombre, @RequestParam String foto, // Use MultipartFile to handle file uploads
                                             @RequestParam int posicion) throws IOException {
        InputStream imageStream = new URL(foto).openStream();

        byte[] fotoBytes = imageStream.readAllBytes();
        Equipo equipo = new Equipo();
        equipo.setNombre(nombre);
        equipo.setFoto(fotoBytes);
        equipo.setPosicion(posicion);


        Equipo us = equipoRepository.findById(equipo.getNombre()).orElse(null);
        us.setNombre(equipo.getNombre());
        us.setPosicion(equipo.getPosicion());
        us.setFoto(equipo.getFoto());
        return equipoRepository.save(us);
    }

}