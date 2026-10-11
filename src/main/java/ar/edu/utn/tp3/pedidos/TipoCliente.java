package ar.edu.utn.tp3.pedidos;

import java.util.List;

/** Tipo de cliente con su tabla de descuentos por cantidad (tramos de mayor a menor). */
public enum TipoCliente {
  MAYORISTA(new Tramo(100, 0.20), new Tramo(50, 0.15), new Tramo(10, 0.10), new Tramo(0, 0.05)),
  VIP(new Tramo(100, 0.25), new Tramo(50, 0.18), new Tramo(0, 0.12)),
  MINORISTA(new Tramo(10, 0.05));

  private final List<Tramo> tramos;

  TipoCliente(Tramo... tramos) {
    this.tramos = List.of(tramos);
  }

  /** Devuelve el descuento del primer tramo cuyo mínimo alcanza la cantidad, o 0 si ninguno. */
  public double descuentoPorCantidad(int cantidad) {
    for (Tramo tramo : tramos) {
      if (cantidad >= tramo.minimo()) {
        return tramo.descuento();
      }
    }
    return 0;
  }

  private record Tramo(int minimo, double descuento) {}
}
