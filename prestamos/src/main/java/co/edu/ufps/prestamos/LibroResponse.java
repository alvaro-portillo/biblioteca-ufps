package co.edu.ufps.prestamos;

public record LibroResponse(
        Long id,
        String titulo,
        Integer ejemplaresDisponibles) {
}
