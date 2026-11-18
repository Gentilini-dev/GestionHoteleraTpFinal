package Gestor;

import Enums.*;
import Excepciones.*;
import Persistencia.JDBC.*;
import Modelos.*;
import Persistencia.Archivos.BackupTXT;
import Colecciones.Catalogo;

import java.time.LocalDate;
import java.util.ArrayList;

// Clase gestora: tiene toda la lógica del hotel y es la única que habla con la base.
// No imprime nada, si algo sale mal tira una excepción y el menú muestra el mensaje.
// En las altas guardamos primero en la base y después en memoria, así si falla la base no queda nada a medias.
public class GestorHotel
{
    // Lo que está cargado en memoria
    private Catalogo<Usuario> usuarios;
    private Catalogo<Pasajero> pasajeros;
    private Catalogo<Habitacion> habitaciones;
    private Catalogo<Salon> salones;
    private ArrayList<Reserva> reservas;
    private ArrayList<Estadia> estadias;
    private ArrayList<BloqueoHabitacion> bloqueos;
    private ArrayList<Evento> eventos;

    // Una conexión por tabla
    private ConexionUsuario conexionUsuario;
    private ConexionPasajero conexionPasajero;
    private ConexionHabitacion conexionHabitacion;
    private ConexionSalon conexionSalon;
    private ConexionReserva conexionReserva;
    private ConexionEstadia conexionEstadia;
    private ConexionBloqueoHabitacion conexionBloqueo;
    private ConexionEvento conexionEvento;

    private BackupTXT backup;

    // Próximo id a usar. Se acomodan cuando cargamos lo que hay en la base
    private int proximoIdUsuario = 1;
    private int proximoIdPasajero = 1;
    private int proximoIdReserva = 1;
    private int proximoIdEstadia = 1;
    private int proximoIdBloqueo = 1;
    private int proximoIdEvento = 1;

    public GestorHotel()
    {
        this.usuarios = new Catalogo<>();
        this.pasajeros = new Catalogo<>();
        this.habitaciones = new Catalogo<>();
        this.salones = new Catalogo<>();
        this.reservas = new ArrayList<>();
        this.estadias = new ArrayList<>();
        this.bloqueos = new ArrayList<>();
        this.eventos = new ArrayList<>();

        // Los DAOs que reconstruyen relaciones reciben los mapas de los catálogos
        this.conexionUsuario = new ConexionUsuario();
        this.conexionPasajero = new ConexionPasajero();
        this.conexionHabitacion = new ConexionHabitacion();
        this.conexionSalon = new ConexionSalon();
        this.conexionReserva = new ConexionReserva(pasajeros.getMapa(), habitaciones.getMapa());
        this.conexionEstadia = new ConexionEstadia(habitaciones.getMapa(), pasajeros.getMapa());
        this.conexionBloqueo = new ConexionBloqueoHabitacion(habitaciones.getMapa());
        this.conexionEvento = new ConexionEvento(salones.getMapa());

        this.backup = new BackupTXT("backup_hotel.txt");
    }

    // ----- Carga inicial -----

    // Se llama una vez al arrancar. Primero van pasajeros, habitaciones y salones
    // porque las reservas, estadías, bloqueos y eventos los necesitan para armarse
    public void cargarDatos() throws ErrorConexionSQLException
    {
        conexionUsuario.crearTabla();
        conexionPasajero.crearTabla();
        conexionHabitacion.crearTabla();
        conexionSalon.crearTabla();
        conexionReserva.crearTabla();
        conexionEstadia.crearTabla();
        conexionBloqueo.crearTabla();
        conexionEvento.crearTabla();

        try
        {
            for (Usuario u : conexionUsuario.traerTodos())
            {
                usuarios.agregar(u);
            }
            for (Pasajero p : conexionPasajero.traerTodos())
            {
                pasajeros.agregar(p);
            }
            for (Habitacion h : conexionHabitacion.traerTodos())
            {
                habitaciones.agregar(h);
            }
            for (Salon s : conexionSalon.traerTodos())
            {
                salones.agregar(s);
            }
        }
        catch (ElementoDuplicadoException e)
        {
            // No debería pasar: la base no admite claves repetidas
            throw new ErrorConexionSQLException("Datos duplicados en la base: " + e.getMessage());
        }

        reservas.addAll(conexionReserva.traerTodos());
        estadias.addAll(conexionEstadia.traerTodos());
        bloqueos.addAll(conexionBloqueo.traerTodos());
        eventos.addAll(conexionEvento.traerTodos());

        ajustarContadores();

        // Primera vez que se usa el sistema: sin un admin nadie podría entrar
        if (usuarios.listar().isEmpty())
        {
            Usuario admin = new UsuarioAdministrador(proximoIdUsuario, "Administrador", "admin", "admin123");
            conexionUsuario.insertar(admin);
            try
            {
                usuarios.agregar(admin);
            }
            catch (ElementoDuplicadoException e)
            {
                // no puede pasar: el catálogo está vacío
            }
            proximoIdUsuario++;
        }
    }

    // Cada contador queda en el mayor id cargado + 1, para no repetir claves primarias
    private void ajustarContadores()
    {
        for (Usuario u : usuarios.listar())
        {
            proximoIdUsuario = Math.max(proximoIdUsuario, u.getId() + 1);
        }
        for (Pasajero p : pasajeros.listar())
        {
            proximoIdPasajero = Math.max(proximoIdPasajero, p.getId() + 1);
        }
        for (Reserva r : reservas)
        {
            proximoIdReserva = Math.max(proximoIdReserva, r.getId() + 1);
        }
        for (Estadia e : estadias)
        {
            proximoIdEstadia = Math.max(proximoIdEstadia, e.getId() + 1);
        }
        for (BloqueoHabitacion b : bloqueos)
        {
            proximoIdBloqueo = Math.max(proximoIdBloqueo, b.getId() + 1);
        }
        for (Evento e : eventos)
        {
            proximoIdEvento = Math.max(proximoIdEvento, e.getId() + 1);
        }
    }

    // ----- Usuarios -----

    // Devuelve el usuario si los datos son correctos, si no lanza la excepción
    public Usuario iniciarSesion(String nombreUsuario, String password) throws DatosInvalidosException
    {
        for (Usuario u : usuarios.listar())
        {
            if (u.validarUsuario(nombreUsuario, password))
            {
                return u;
            }
        }
        throw new DatosInvalidosException("Usuario o contraseña incorrectos.");
    }

    // Solo lo usa el administrador. El rol define qué menú va a tener el usuario nuevo
    public Usuario crearUsuario(String nombre, String nombreUsuario, String password, RolUsuario rol)
            throws ElementoDuplicadoException, DatosInvalidosException, ErrorConexionSQLException
    {
        if (nombreUsuario.isBlank() || password.isBlank())
        {
            throw new DatosInvalidosException("El usuario y la contraseña no pueden estar vacíos.");
        }
        for (Usuario existente : usuarios.listar())
        {
            if (existente.getUsuario().equalsIgnoreCase(nombreUsuario))
            {
                throw new ElementoDuplicadoException("Ya existe el usuario " + nombreUsuario + ".");
            }
        }

        Usuario nuevo;
        if (rol == RolUsuario.ADMINISTRADOR)
        {
            nuevo = new UsuarioAdministrador(proximoIdUsuario, nombre, nombreUsuario, password);
        }
        else
        {
            nuevo = new UsuarioRecepcionista(proximoIdUsuario, nombre, nombreUsuario, password);
        }

        conexionUsuario.insertar(nuevo);
        usuarios.agregar(nuevo);
        proximoIdUsuario++;
        return nuevo;
    }

    public ArrayList<Usuario> listarUsuarios()
    {
        return usuarios.listar();
    }

    // ----- Altas -----

    // El id lo pone el sistema, el DNI no se puede repetir
    public Pasajero registrarPasajero(String nombre, String apellido, String dni, String domicilioOrigen, String origen)
            throws ElementoDuplicadoException, ErrorConexionSQLException
    {
        for (Pasajero existente : pasajeros.listar())
        {
            if (existente.getDni().equals(dni))
            {
                throw new ElementoDuplicadoException("Ya existe un pasajero con DNI " + dni + ".");
            }
        }

        Pasajero p = new Pasajero(nombre, apellido, dni, domicilioOrigen, origen, proximoIdPasajero);
        conexionPasajero.insertar(p);
        pasajeros.agregar(p);
        proximoIdPasajero++;
        return p;
    }

    public void agregarHabitacion(Habitacion h) throws ElementoDuplicadoException, ErrorConexionSQLException
    {
        if (habitaciones.existe(h.getId()))
        {
            throw new ElementoDuplicadoException("Ya existe la habitación " + h.getId() + ".");
        }
        conexionHabitacion.insertar(h);
        habitaciones.agregar(h);
    }

    public void agregarSalon(Salon s) throws ElementoDuplicadoException, ErrorConexionSQLException
    {
        if (salones.existe(s.getId()))
        {
            throw new ElementoDuplicadoException("Ya existe un salón con id " + s.getId() + ".");
        }
        conexionSalon.insertar(s);
        salones.agregar(s);
    }

    // ----- Disponibilidad -----

    // Dos períodos se pisan si cada uno empieza antes de que termine el otro.
    // El día de salida de uno puede ser el de entrada del siguiente.
    private boolean seSuperponen(LocalDate desde1, LocalDate hasta1, LocalDate desde2, LocalDate hasta2)
    {
        return desde1.isBefore(hasta2) && desde2.isBefore(hasta1);
    }

    // Libre = sin reservas vigentes (confirmadas o en curso) ni bloqueos en ese período
    private boolean estaLibre(int nro, LocalDate desde, LocalDate hasta)
    {
        for (Reserva r : reservas)
        {
            if (r.getHabitacion().getId() == nro
                    && (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.EN_CURSO)
                    && seSuperponen(r.getDesde(), r.getHasta(), desde, hasta))
            {
                return false;
            }
        }

        for (BloqueoHabitacion b : bloqueos)
        {
            if (b.getHabitacion().getId() == nro && seSuperponen(b.getDesde(), b.getHasta(), desde, hasta))
            {
                return false;
            }
        }

        return true;
    }

    public boolean estaDisponible(int nro, LocalDate desde, LocalDate hasta)
            throws ElementoNoEncontradoException, DatosInvalidosException
    {
        if (desde.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede consultar una fecha pasada.");
        }
        if (!hasta.isAfter(desde))
        {
            throw new DatosInvalidosException("La fecha hasta tiene que ser posterior a la fecha desde.");
        }
        habitaciones.buscar(nro);   // si la habitación no existe, lanza la excepción
        return estaLibre(nro, desde, hasta);
    }

    // ----- Pasajeros -----

    public Pasajero buscarPasajeroPorDni(String dni) throws ElementoNoEncontradoException
    {
        for (Pasajero p : pasajeros.listar())
        {
            if (p.getDni().equals(dni))
            {
                return p;
            }
        }
        throw new ElementoNoEncontradoException("No se encontró ningún pasajero con DNI " + dni + ".");
    }

    public ArrayList<Pasajero> listarPasajeros()
    {
        return pasajeros.listar();
    }

    public ArrayList<Reserva> reservasDePasajero(String dni) throws ElementoNoEncontradoException
    {
        Pasajero pasajero = buscarPasajeroPorDni(dni);

        ArrayList<Reserva> reservasPasajero = new ArrayList<>();
        for (Reserva reserva : reservas)
        {
            if (reserva.getTitular().getDni().equals(pasajero.getDni()))
            {
                reservasPasajero.add(reserva);
            }
        }
        return reservasPasajero;
    }

    // Todas las estadías (activas y finalizadas) en las que el pasajero fue ocupante
    public ArrayList<Estadia> historialPasajero(String dni) throws ElementoNoEncontradoException
    {
        Pasajero p = buscarPasajeroPorDni(dni);

        ArrayList<Estadia> historial = new ArrayList<>();
        for (Estadia e : estadias)
        {
            if (e.getOcupantes().contains(p))
            {
                historial.add(e);
            }
        }
        return historial;
    }

    // ----- Reservas -----

    public ArrayList<Reserva> listarReservas()
    {
        return reservas;
    }

    // Las que todavía se pueden tocar: confirmadas (cancelar o check-in) y en curso (check-out)
    public ArrayList<Reserva> listarReservasVigentes()
    {
        ArrayList<Reserva> vigentes = new ArrayList<>();
        for (Reserva r : reservas)
        {
            if (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.EN_CURSO)
            {
                vigentes.add(r);
            }
        }
        return vigentes;
    }

    public Reserva crearReserva(String dni, int nro, LocalDate desde, int noches)
            throws DatosInvalidosException, ElementoNoEncontradoException, HabitacionNoDisponibleException,
                   ErrorConexionSQLException
    {
        if (noches < 1)
        {
            throw new DatosInvalidosException("La reserva debe ser de al menos una noche.");
        }
        if (desde.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede reservar en una fecha pasada.");
        }

        LocalDate hasta = desde.plusDays(noches);
        Pasajero titular = buscarPasajeroPorDni(dni);
        Habitacion habitacion = habitaciones.buscar(nro);

        if (!estaLibre(nro, desde, hasta))
        {
            throw new HabitacionNoDisponibleException(nro, desde, hasta);
        }

        Reserva r = new Reserva(proximoIdReserva, titular, habitacion, desde, hasta, EstadoReserva.CONFIRMADA);
        conexionReserva.insertar(r);
        reservas.add(r);
        proximoIdReserva++;
        return r;
    }

    private Reserva buscarReserva(int idReserva) throws ElementoNoEncontradoException
    {
        for (Reserva r : reservas)
        {
            if (r.getId() == idReserva)
            {
                return r;
            }
        }
        throw new ElementoNoEncontradoException("No existe una reserva con id " + idReserva + ".");
    }

    // Solo se cancela una reserva confirmada (no una en curso, cancelada o finalizada)
    public void cancelarReserva(int idReserva)
            throws ElementoNoEncontradoException, DatosInvalidosException, ErrorConexionSQLException
    {
        Reserva r = buscarReserva(idReserva);

        if (r.getEstado() != EstadoReserva.CONFIRMADA)
        {
            throw new DatosInvalidosException("La reserva " + idReserva
                    + " no se puede cancelar porque está " + r.getEstado() + ".");
        }

        r.setEstado(EstadoReserva.CANCELADA);
        conexionReserva.actualizar(r);
    }

    // ----- Estadías (check-in / check-out) -----

    // true si hay alguien alojado ahora (que no haya nadie no es un error)
    private boolean tieneEstadiaActiva(int nro)
    {
        for (Estadia e : estadias)
        {
            if (e.getHabitacion().getId() == nro && e.getEstado() == EstadoEstadia.ACTIVA)
            {
                return true;
            }
        }
        return false;
    }

    // Igual que el de arriba pero para el check-out, acá si no hay nadie sí es un error
    private Estadia buscarEstadiaActiva(int nro) throws ElementoNoEncontradoException
    {
        for (Estadia e : estadias)
        {
            if (e.getHabitacion().getId() == nro && e.getEstado() == EstadoEstadia.ACTIVA)
            {
                return e;
            }
        }
        throw new ElementoNoEncontradoException("La habitación " + nro + " no tiene una estadía activa.");
    }

    // El titular entra siempre; los acompañantes se indican por DNI (pueden ser cero)
    public Estadia checkIn(int idReserva, ArrayList<String> dnisAcompanantes)
            throws ElementoNoEncontradoException, DatosInvalidosException, ErrorConexionSQLException
    {
        Reserva r = buscarReserva(idReserva);

        if (r.getEstado() != EstadoReserva.CONFIRMADA)
        {
            throw new DatosInvalidosException("La reserva " + idReserva
                    + " no admite check-in porque está " + r.getEstado() + ".");
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(r.getDesde()) || !hoy.isBefore(r.getHasta()))
        {
            throw new DatosInvalidosException("El check-in de la reserva " + idReserva
                    + " solo puede hacerse entre el " + r.getDesde() + " y el " + r.getHasta().minusDays(1) + ".");
        }

        int nro = r.getHabitacion().getId();
        if (tieneEstadiaActiva(nro))
        {
            throw new DatosInvalidosException("La habitación " + nro + " ya tiene un check-in activo.");
        }

        ArrayList<Pasajero> ocupantes = new ArrayList<>();
        ocupantes.add(r.getTitular());
        for (String dni : dnisAcompanantes)
        {
            Pasajero p = buscarPasajeroPorDni(dni);
            if (!ocupantes.contains(p))   // evita cargar dos veces a la misma persona
            {
                ocupantes.add(p);
            }
        }

        Estadia e = new Estadia(proximoIdEstadia, r.getHabitacion(), ocupantes);
        conexionEstadia.insertar(e);
        estadias.add(e);
        proximoIdEstadia++;

        r.setEstado(EstadoReserva.EN_CURSO);
        conexionReserva.actualizar(r);
        return e;
    }

    // Cierra la estadía activa y finaliza la reserva que la originó: la habitación queda libre
    public Estadia checkOut(int nro) throws ElementoNoEncontradoException, ErrorConexionSQLException
    {
        Estadia e = buscarEstadiaActiva(nro);
        e.finalizar();
        conexionEstadia.actualizar(e);

        for (Reserva r : reservas)
        {
            if (r.getHabitacion().getId() == nro && r.getEstado() == EstadoReserva.EN_CURSO)
            {
                r.setEstado(EstadoReserva.FINALIZADA);
                conexionReserva.actualizar(r);
            }
        }
        return e;
    }

    // ----- Bloqueos y listados de habitaciones -----

    public BloqueoHabitacion bloquearHabitacion(int nro, MotivoBloqueo motivo, String detalle, LocalDate desde, int dias)
            throws DatosInvalidosException, ElementoNoEncontradoException, HabitacionNoDisponibleException,
                   ErrorConexionSQLException
    {
        if (dias < 1)
        {
            throw new DatosInvalidosException("No se puede bloquear una habitación por " + dias + " días.");
        }
        if (desde.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede bloquear una habitación en una fecha pasada.");
        }

        LocalDate hasta = desde.plusDays(dias);
        Habitacion h = habitaciones.buscar(nro);

        if (!estaLibre(nro, desde, hasta))
        {
            throw new HabitacionNoDisponibleException(nro, desde, hasta);
        }

        BloqueoHabitacion b = new BloqueoHabitacion(proximoIdBloqueo, h, motivo, detalle, desde, hasta);
        conexionBloqueo.insertar(b);
        bloqueos.add(b);
        proximoIdBloqueo++;
        return b;
    }

    // Ocupadas = con una estadía activa (se devuelven las estadías porque tienen los ocupantes)
    public ArrayList<Estadia> listarOcupadas()
    {
        ArrayList<Estadia> ocupadas = new ArrayList<>();
        for (Estadia e : estadias)
        {
            if (e.getEstado() == EstadoEstadia.ACTIVA)
            {
                ocupadas.add(e);
            }
        }
        return ocupadas;
    }

    // Disponibles = se pueden ocupar esta noche
    public ArrayList<Habitacion> listarDisponibles()
    {
        ArrayList<Habitacion> disponibles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (Habitacion h : habitaciones.listar())
        {
            if (estaLibre(h.getId(), hoy, hoy.plusDays(1)))
            {
                disponibles.add(h);
            }
        }
        return disponibles;
    }

    // No disponibles = con un bloqueo vigente hoy (se devuelven los bloqueos porque tienen el motivo)
    public ArrayList<BloqueoHabitacion> listarNoDisponibles()
    {
        ArrayList<BloqueoHabitacion> noDisponibles = new ArrayList<>();
        for (BloqueoHabitacion b : bloqueos)
        {
            if (b.estaActivo())
            {
                noDisponibles.add(b);
            }
        }
        return noDisponibles;
    }

    // ----- Salones y eventos -----

    public ArrayList<Salon> listarSalones()
    {
        return salones.listar();
    }

    public ArrayList<Evento> listarEventos()
    {
        return eventos;
    }

    private Evento buscarEvento(int idEvento) throws ElementoNoEncontradoException
    {
        for (Evento e : eventos)
        {
            if (e.getId() == idEvento)
            {
                return e;
            }
        }
        throw new ElementoNoEncontradoException("No existe un evento con id " + idEvento + ".");
    }

    // Un salón admite un solo evento confirmado por día y no puede superar su capacidad
    public Evento reservarSalon(int idSalon, LocalDate fecha, String organizador, int asistentes)
            throws DatosInvalidosException, ElementoNoEncontradoException, SalonNoDisponibleException,
                   ErrorConexionSQLException
    {
        if (fecha.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede reservar un salón en una fecha pasada.");
        }

        Salon salon = salones.buscar(idSalon);

        if (asistentes < 1 || asistentes > salon.getCapacidad())
        {
            throw new DatosInvalidosException("La cantidad de asistentes debe estar entre 1 y "
                    + salon.getCapacidad() + " (capacidad del salón " + salon.getNombre() + ").");
        }

        for (Evento e : eventos)
        {
            if (e.getSalon().getId() == idSalon && e.getEstado() == EstadoEvento.CONFIRMADO
                    && e.getFecha().equals(fecha))
            {
                throw new SalonNoDisponibleException(salon.getNombre(), fecha);
            }
        }

        Evento evento = new Evento(proximoIdEvento, salon, organizador, fecha, asistentes);
        conexionEvento.insertar(evento);
        eventos.add(evento);
        proximoIdEvento++;
        return evento;
    }

    public void cancelarEvento(int idEvento)
            throws ElementoNoEncontradoException, DatosInvalidosException, ErrorConexionSQLException
    {
        Evento e = buscarEvento(idEvento);

        if (e.getEstado() != EstadoEvento.CONFIRMADO)
        {
            throw new DatosInvalidosException("El evento " + idEvento
                    + " no se puede cancelar porque está " + e.getEstado() + ".");
        }

        e.cancelar();
        conexionEvento.actualizar(e);
    }

    // ----- Backup (solo admin) -----

    // Escribe en el archivo todos los datos del hotel y devuelve la ruta del backup
    public String realizarBackup() throws ErrorArchivoException
    {
        ArrayList<String> titulos = new ArrayList<>();
        ArrayList<ArrayList<?>> secciones = new ArrayList<>();

        titulos.add("PASAJEROS");
        secciones.add(pasajeros.listar());
        titulos.add("HABITACIONES");
        secciones.add(habitaciones.listar());
        titulos.add("SALONES");
        secciones.add(salones.listar());
        titulos.add("RESERVAS");
        secciones.add(reservas);
        titulos.add("ESTADIAS");
        secciones.add(estadias);
        titulos.add("BLOQUEOS");
        secciones.add(bloqueos);
        titulos.add("EVENTOS");
        secciones.add(eventos);

        backup.exportar(titulos, secciones);
        return backup.getRuta();
    }

    public ArrayList<String> leerBackup() throws ErrorArchivoException
    {
        return backup.leer();
    }
}
