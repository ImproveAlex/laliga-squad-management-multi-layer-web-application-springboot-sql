package com.practicaFinalTIW.front.controllers;

import java.util.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaFinalTIW.front.domains.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.UriUtils;

@Controller
public class Consumer {

    @Autowired
    RestTemplate restTemplate;
    private final String usersBaseApiUrl = "http://localhost:8031/usuarios";
    private final String jugadoreBaseApiUrl = "http://localhost:8032/jugadores";
    private final String equiposBaseApiUrl = "http://localhost:8032/equipos";

    private final String chatApiUrl = "http://localhost:8033/mensajes";

    public Consumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String index(Model model, @CookieValue(value = "correo", defaultValue = "") String correo,
            @RequestParam(required = false) String addcookie) {
        ParameterizedTypeReference<List<EquipoRespuesta>> responseType = new ParameterizedTypeReference<List<EquipoRespuesta>>() {
        };


        ResponseEntity<List<EquipoRespuesta>> responseEntity = restTemplate.exchange(equiposBaseApiUrl, HttpMethod.GET, null,
                responseType);
        List<EquipoRespuesta> equiposRespuestas = responseEntity.getBody();

        List<Equipo> equipos = new ArrayList<>();

        for (EquipoRespuesta equiporesp : equiposRespuestas) {
            Equipo nuevoEquipo = new Equipo();
            nuevoEquipo = equiporespToEquipo(equiporesp);
            equipos.add(nuevoEquipo);
        }


        if (addcookie != null && !addcookie.equals("")) {
            correo = addcookie;
            model.addAttribute("addcookie", correo);
        }
        if (correo == null || correo.equals("")) {
            Collections.sort(equipos, new Comparator<Equipo>() {
                @Override
                public int compare(Equipo e1, Equipo e2) {
                    return Integer.compare(e1.getPosicion(), e2.getPosicion());
                }
            });
            model.addAttribute("equipos", equipos);
            return "index";
        }
        Usuario newUser;
        try {
            newUser = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            Collections.sort(equipos, new Comparator<Equipo>() {
                @Override
                public int compare(Equipo e1, Equipo e2) {
                    return Integer.compare(e1.getPosicion(), e2.getPosicion());
                }
            });
            model.addAttribute("equipos", equipos);
            model.addAttribute("addcookie", "");
            return "index";
        }

        Collections.sort(equipos, new Comparator<Equipo>() {
            @Override
            public int compare(Equipo e1, Equipo e2) {
                if (newUser !=null && newUser.getEquipo() != null ) {
                    if (newUser.getEquipo().equals(e1.getNombre())) {
                        return -1;
                    } else if (newUser.getEquipo().equals(e2.getNombre())) {
                        return 1;
                    }
                }
                return Integer.compare(e1.getPosicion(), e2.getPosicion());
            }
        });

        model.addAttribute("equipos", equipos);
        model.addAttribute("user", newUser);
        return "index";
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////// USUARIOS ////////////////////////////
    //////////////////////////////////////////////////////////////////

    @GetMapping("/login")
    public String login(Model model) {
        return "users/login.html";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String correo,
            @RequestParam String contrasena) {
        Usuario newUser;
        try {
            newUser = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            model.addAttribute("loginerror", "Usuario o contraseña erróneos");
            return "users/login.html";
        }

        if (newUser.getContrasena().equals(contrasena)) {
            return "redirect:/?addcookie=" + correo;
        } else {
            model.addAttribute("loginerror", "Usuario o contraseña erróneos");
            return "users/login.html";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        ParameterizedTypeReference<List<Equipo>> responseType = new ParameterizedTypeReference<List<Equipo>>() {
        };

        ResponseEntity<List<Equipo>> responseEntity = restTemplate.exchange(equiposBaseApiUrl, HttpMethod.GET, null,
                responseType);
        List<Equipo> equipos = responseEntity.getBody();
        model.addAttribute("equipos", equipos);
        return "users/register.html";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute Usuario us) {
        // Check if all fields are filled

        if (us.getNombre().equals("") || us.getApellidos().equals("") || us.getCorreo().equals("")
                || us.getContrasena().equals("")) {
            model.addAttribute("registererror", "Rellene todos los campos");
            return "users/register.html";
        }
        // Change empty teams for null value for the database
        if (us.getEquipo().equals("")) {
            us.setEquipo(null);
        }

        Usuario newUser = restTemplate.postForObject(usersBaseApiUrl, us, Usuario.class);

        if (newUser == null) {
            model.addAttribute("registererror", "El correo ya está registrado");
            return "users/register.html";
        }

        model.addAttribute("Usuario", newUser);
        return "redirect:/?addcookie=" + newUser.getCorreo();
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("addcookie", "");
        return "index";
    }

    @GetMapping("/users/edit/{correo}")
    public String showEditForm(@PathVariable String correo, Model model) {

        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            return "index";
        }

        model.addAttribute("usuario", user);

        ParameterizedTypeReference<List<Equipo>> responseType = new ParameterizedTypeReference<List<Equipo>>() {
        };

        ResponseEntity<List<Equipo>> responseEntity = restTemplate.exchange(equiposBaseApiUrl, HttpMethod.GET, null,
                responseType);
        List<Equipo> equipos = responseEntity.getBody();
        model.addAttribute("equipos", equipos);

        return "users/editUser.html";
    }

    @PostMapping("users/edit/{correo}")
    public String editUser(@PathVariable String correo, @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String password, @RequestParam String team, Model model) {

        Usuario changeUser;
        try {
            changeUser = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            return "error.html";
        }

        changeUser.setNombre(firstName);
        changeUser.setApellidos(lastName);
        if (!password.equals("")) {
            changeUser.setContrasena(password);
        }
        changeUser.setEquipo(team);

        restTemplate.put(usersBaseApiUrl, changeUser);

        return "redirect:/";
    }

    @GetMapping("/users/delete/{correo}")
    public String viewDeleteUser(@PathVariable String correo, Model model) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            return "index";
        }

        model.addAttribute("user", user);

        return "users/deleteUser";
    }

    @PostMapping("/users/delete/{correo}")
    public String deleteUser(@PathVariable String correo, Model model) {

        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
        } catch (RestClientException e) {
            return "error.html";
        }

        restTemplate.delete(usersBaseApiUrl + "?correo={correo}", user.getCorreo());

        return "redirect:/";
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////// EQUIPOS ////////////////////////////
    //////////////////////////////////////////////////////////////////

    @GetMapping("/equipo/{nombre}")
    public String getMethodName(Model model, @CookieValue(value = "correo", defaultValue = "") String correo,
            @RequestParam(required = false) String addcookie, @PathVariable String nombre) {

        EquipoRespuesta equipo;
        try {

            equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", EquipoRespuesta.class, nombre);
            // Parseamos la foto para ser visible por thymeleaf
            String base64Image = parsearFotoEquipo(equipo.getFoto());
            model.addAttribute("base64Image", base64Image);
        } catch (RestClientException e) {
            return "index";
        }

        ParameterizedTypeReference<List<Jugador>> responseType = new ParameterizedTypeReference<List<Jugador>>() {
        };
        ResponseEntity<List<Jugador>> responseEntity = restTemplate.exchange(jugadoreBaseApiUrl + "/equipo/" + nombre,
                HttpMethod.GET, null,
                responseType);
        List<Jugador> jugadores = responseEntity.getBody();

        List<Jugador> delanteros = new ArrayList<Jugador>();
        List<Jugador> medios = new ArrayList<Jugador>();
        List<Jugador> defensas = new ArrayList<Jugador>();
        Jugador portero = null;
        List<Jugador> suplentes = new ArrayList<Jugador>();

        for (Jugador jugador : jugadores) {
            if (jugador.getPosicion().equals("delantero") && delanteros.size() < 3) {
                delanteros.add(jugador);
            } else if (jugador.getPosicion().equals("medio") && medios.size() < 4) {
                medios.add(jugador);
            } else if (jugador.getPosicion().equals("defensa") && defensas.size() < 4) {
                defensas.add(jugador);
            } else if (jugador.getPosicion().equals("portero") && portero == null) {
                portero = jugador;
            } else {
                suplentes.add(jugador);
            }
        }

        model.addAttribute("delanteros", delanteros);
        model.addAttribute("medios", medios);
        model.addAttribute("defensas", defensas);
        model.addAttribute("portero", portero);
        model.addAttribute("suplentes", suplentes);

        model.addAttribute("equipo", equipo);
        try {
            Usuario user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);
            model.addAttribute("user", user);
        } catch (RestClientException e) {

        }
        return "teams/team";
    }

    @GetMapping(value = "/crearEquipo")
    public String createTeam(Model model, @CookieValue(value = "correo", defaultValue = "") String correo,
            HttpServletResponse response) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }

        // Continue with the rest of your logic
        return "teams/createTeam";
    }

    @PostMapping(value = "/crearEquipo")
    public String createTeam(Model model, @CookieValue(value = "correo", defaultValue = "") String correo,
            @RequestParam String nombre, @RequestParam String rutaFoto, @RequestParam String posicion,
            HttpServletResponse response) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }

        Equipo newEquipo = new Equipo(nombre, rutaFoto, Integer.parseInt(posicion));
        String urlWithParams = String.format("%s?nombre=%s&foto=%s&posicion=%s",
                equiposBaseApiUrl, nombre, rutaFoto, posicion);
        Equipo equipo = restTemplate.postForObject( urlWithParams,null, Equipo.class);
        if (equipo == null) {
            model.addAttribute("error", "El equipo ya existe");
            return "teams/createTeam";
        }

        return "redirect:/";
    }

    @GetMapping("/equipo/editar/{nombre}")
    public String showEditTeamForm(@PathVariable String nombre, Model model,
            @CookieValue(value = "correo", defaultValue = "") String correo, HttpServletResponse response) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }

        EquipoRespuesta equiporesp;
        try {
            equiporesp = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", EquipoRespuesta.class, nombre);
        } catch (RestClientException e) {
            return "index";
        }

        Equipo equipo = equiporespToEquipo(equiporesp);

        model.addAttribute("equipo", equipo);

        return "teams/editTeam";
    }

    @PostMapping("/equipo/editar/{nombre}")
    public String EditTeam(Model model, @CookieValue(value = "correo", defaultValue = "") String correo,
            @PathVariable String nombre, @RequestParam String rutaFoto, @RequestParam String posicion,
            @RequestParam String action,
            HttpServletResponse response) {

        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }

        Equipo equipo;
        try {
            equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
        } catch (RestClientException e) {
            return "index";
        }

        if (action.equals("editTeam")) {
            equipo.setRutaFoto(rutaFoto);
            equipo.setPosicion(Integer.parseInt(posicion));

            try {
                restTemplate.put(equiposBaseApiUrl, equipo);
            } catch (RestClientException e) {
                return "redirect:/";
            }
        } else if (action.equals("delete")) {
            restTemplate.delete(equiposBaseApiUrl + "?nombre={nombre}", equipo.getNombre());
        } else {
            return "redirect:/error";
        }

        return "redirect:/";
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////// JUGADORES ////////////////////////////
    //////////////////////////////////////////////////////////////////

    @GetMapping("/equipo/editar/nuevoJugador/{nombre}")
    public String showNewPlayerForm(@PathVariable String nombre, Model model,
            @CookieValue(value = "correo", defaultValue = "") String correo,
            HttpServletResponse response) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }

        Equipo equipo;
        try {
            equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
        } catch (RestClientException e) {
            return "index";
        }

        model.addAttribute("equipo", equipo);

        return "players/addPlayer.html";
    }

    @PostMapping("/equipo/editar/nuevoJugador/{nombre}")
    public String addNewPlayerForm(@PathVariable String nombre, Model model,
            @CookieValue(value = "correo", defaultValue = "") String correo,
            @RequestParam String dni, @RequestParam String nombreJugador,
            @RequestParam String apellidos, @RequestParam String posicion,
            @RequestParam String alias, @RequestParam String rutaFoto,
            HttpServletResponse response) {
        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user == null || !"admin".equals(user.getRol())) {
                // Set 403 Forbidden status
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "forbidden"; // Replace with the appropriate view name for forbidden state
            }
        } catch (RestClientException e) {
            // Set 403 Forbidden status
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "forbidden"; // Replace with the appropriate view name for forbidden state
        }
        Jugador newJugador = new Jugador();
        try {
            newJugador.setDni(dni);
            newJugador.setNombre(nombreJugador);
            newJugador.setApellidos(apellidos);
            newJugador.setPosicion(posicion);
            newJugador.setAlias(alias);
            newJugador.setRutaFoto(rutaFoto);
            newJugador.setEquipo(nombre);
        } catch (RestClientException e) {
            model.addAttribute("error", "Error creando el jugador");
            Equipo equipo;
            try {
                equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
            } catch (RestClientException ee) {
                return "index";
            }

            model.addAttribute("equipo", equipo);
            return "players/addPlayer.html";
        }

        try {
            Jugador jugador = restTemplate.postForObject(jugadoreBaseApiUrl, newJugador, Jugador.class);
            Equipo equipo;
            nombre = UriUtils.encode(nombre, StandardCharsets.UTF_8);
            equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
        } catch (RestClientException e) {
            
            if (e.getMessage().contains("El Jugador ya está registrado")){
                model.addAttribute("error", "El jugador ya existe");
                Equipo equipo;
                try {
                    equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
                } catch (RestClientException ee) {
                    return "index";
                }

                model.addAttribute("equipo", equipo);
                return "players/addPlayer";
            }
            
            else {
                model.addAttribute("error", "Límite de " + posicion + "s alcanzado");

                Equipo equipo;
                try {
                    equipo = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", Equipo.class, nombre);
                } catch (RestClientException ee) {
                    return "index";
                }

                model.addAttribute("equipo", equipo);
                return "players/addPlayer";
            }
        }

        return "redirect:/equipo/" + nombre;
    }

    @GetMapping("/equipo/jugador/{dni}")
    public String viewJugadorString(Model model,
            @CookieValue(value = "correo", defaultValue = "") String correo,
            @PathVariable String dni) {

        Jugador jugador;
        try {
            jugador = restTemplate.getForObject(jugadoreBaseApiUrl + "/{dni}", Jugador.class, dni);
        } catch (RestClientException e) {
            return "error.html";
        }
        model.addAttribute("jugador", jugador);

        Equipo equipo;

        try {
            EquipoRespuesta equiporesp = restTemplate.getForObject(equiposBaseApiUrl + "/{nombre}", EquipoRespuesta.class, jugador.getEquipo());
            // Parseamos la foto para ser visible por thymeleaf
            equipo = equiporespToEquipo(equiporesp);
        } catch (RestClientException e) {
            return "error.html";
        }

        model.addAttribute("equipo", equipo);

        Usuario user;
        try {
            user = restTemplate.getForObject(usersBaseApiUrl + "/{correo}", Usuario.class, correo);

            // Check if the user is not found or is not an admin
            if (user != null && "admin".equals(user.getRol())) {
                model.addAttribute("admin", true);
            }
        } catch (RestClientException e) {
        }

        return "players/viewPlayer";
    }

    @PostMapping("/equipo/jugador/{dni}")
    public String EditorDeletePlayer(Model model,
            @CookieValue(value = "correo", defaultValue = "") String correo,
            @PathVariable String dni, @RequestParam String action,
            @RequestParam String nombre, @RequestParam String apellidos,
            @RequestParam String posicion, @RequestParam String alias,
            @RequestParam String rutaFoto) {

        Jugador jugador;
        try {
            jugador = restTemplate.getForObject(jugadoreBaseApiUrl + "/{dni}", Jugador.class, dni);
        } catch (RestClientException e) {
            return "error.html";
        }

        if (action.equals("edit")) {

            jugador.setNombre(nombre);
            jugador.setApellidos(apellidos);
            jugador.setPosicion(posicion);
            jugador.setAlias(alias);
            jugador.setRutaFoto(rutaFoto);

            try {
                restTemplate.put(jugadoreBaseApiUrl, jugador);
            } catch (RestClientException e) {
                model.addAttribute("error", "Error editando el jugador");
            }
        } else if (action.equals("delete")) {
            String nombreEquipo = jugador.getEquipo();
            restTemplate.delete(jugadoreBaseApiUrl + "?dni={dni}", jugador.getDni());
            return "redirect:/equipo/" + nombreEquipo;
        } else {
            return "redirect:/error";
        }

        return "redirect:/equipo/jugador/" + jugador.getDni();
    }
    @GetMapping("/chat")
    public String showChat(@CookieValue(value = "correo", defaultValue = "") String emailori, Model model) {
        String emaildest = emailori;
        if (emailori != null && !emailori.isEmpty()) {
            // If emailori is provided, retrieve messages for the specified user
            Mensaje[] mensajes = restTemplate.getForObject(chatApiUrl + "/ori?emailori=" + emailori, Mensaje[].class);
            Mensaje[] mensajes2 = restTemplate.getForObject(chatApiUrl + "/dest?emaildest=" + emaildest, Mensaje[].class);
            model.addAttribute("mensajes",mensajes);
            model.addAttribute("mensajes2",mensajes2);
        } else {
            // If no emailori is provided, set an empty array
            model.addAttribute("mensajes", new Mensaje[0]);
            model.addAttribute("mensajes2", new Mensaje[0]);
        }

        model.addAttribute("newMensaje", new Mensaje());
        return "chat";
    }
    @PostMapping("/sendmessage")
    public String sendmessage(@ModelAttribute("newMensaje") Mensaje mensaje,@CookieValue(value = "correo", defaultValue = "" )String emailori) {

        // Convert Mensaje object to JSON using Jackson ObjectMapper

        mensaje.setEmailori(emailori);
        try {
            // Attempt to retrieve the Usuario based on email
            Usuario usuario = restTemplate.getForObject(usersBaseApiUrl + "/" + mensaje.getEmaildest(), Usuario.class);

            if (usuario != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonMensaje = objectMapper.writeValueAsString(mensaje);

                    // Set headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    // Create HttpEntity with headers and JSON body
                    HttpEntity<String> request = new HttpEntity<>(jsonMensaje, headers);

                    // Make the request
                    restTemplate.postForObject(chatApiUrl, request, String.class);

                }
                catch (JsonProcessingException e) {
                    e.printStackTrace();
                    // Handle the exception as needed
                }
            }

        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/chat";
    }

    String parsearFotoEquipo(byte[] foto){
        String base64Image ;
        try{
             base64Image = Base64.getEncoder().encodeToString(foto);
        } catch (Exception e){
            base64Image = "";
        }
        return base64Image;
        
    }

    Equipo equiporespToEquipo(EquipoRespuesta equiporesp){
        Equipo newEquipo = new Equipo();
        newEquipo.setNombre(equiporesp.getNombre());
        newEquipo.setPosicion(equiporesp.getPosicion());
        newEquipo.setRutaFoto(parsearFotoEquipo(equiporesp.getFoto()));

        return newEquipo;
    }

}