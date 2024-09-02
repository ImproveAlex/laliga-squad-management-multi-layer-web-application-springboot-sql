package com.practicaFinalTIW.chat.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.practicaFinalTIW.chat.domains.Mensaje;
import com.practicaFinalTIW.chat.repositories.ChatRepository;

@RestController
@CrossOrigin
@EnableAutoConfiguration
public class ChatController {

    @Autowired
    ChatRepository chatRepository;

    @RequestMapping(value = "/mensajes", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getConversaciones() {

        try {
            List<Mensaje> entityList = chatRepository.findAll();

            return new ResponseEntity<>(entityList, HttpStatus.OK);

        } catch (Exception ex) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/mensajes/ori", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getMensajesUsuario(@RequestParam(value = "emailori", required = true) String emailori) {
        System.out.println("IM IN BOYS");
        System.out.println("IM IN BOYS");
        System.out.println("IM IN BOYS");
        System.out.println("IM IN BOYS");

        try {

            List<Mensaje> entityList = chatRepository.findByEmailori(emailori);

            return new ResponseEntity<>(entityList, HttpStatus.OK);

        } catch (Exception ex) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @RequestMapping(value = "/mensajes/dest", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getMensajesUsuariofinal(@RequestParam(value = "emaildest", required = true) String emaildest) {

        try {

            List<Mensaje> entityList = chatRepository.findByEmaildest(emaildest);

            return new ResponseEntity<>(entityList, HttpStatus.OK);

        } catch (Exception ex) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @RequestMapping(value = "/mensajes", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Void> sendMensaje(@RequestBody Mensaje mensaje) {

        try {

            chatRepository.save(mensaje);
            return new ResponseEntity<Void>(HttpStatus.CREATED);

        } catch (Exception e) {

            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/mensajes/{emailori}/{emaildest}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getConversacionesEntreUsuarios(@PathVariable(value = "emailori", required = true) String emailori,
                                               @PathVariable(value = "emaildest", required = true) String emaildest) {

        try {

            List<Mensaje> entityList = chatRepository.findByEmailoriAndEmaildest(emailori, emaildest);

            if (entityList.isEmpty()) {

                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

            } else {

                return new ResponseEntity<>(entityList, HttpStatus.OK);
            }

        } catch (Exception e) {

            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        }
    }


}
