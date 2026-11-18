package Enums;

// CONFIRMADA -> EN_CURSO con el check-in -> FINALIZADA con el check-out.
// Mientras está confirmada se puede pasar a CANCELADA
public enum EstadoReserva
{
    CONFIRMADA,
    CANCELADA,
    EN_CURSO,
    FINALIZADA
}
