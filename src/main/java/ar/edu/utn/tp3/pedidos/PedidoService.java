package ar.edu.utn.tp3.pedidos;

import java.util.HashMap;
import java.util.Map;

/** Servicio de pedidos del depósito: descuentos, envío, total y control de stock. */
public class PedidoService {

  private static final double IVA = 0.21;
  private static final double DESCUENTO_URGENTE = 0.02;

  private final Map<String, Integer> stock = new HashMap<>();

  public PedidoService() {
    stock.put("MANCUERNA-10", 50);
    stock.put("BANCO-PLANO", 8);
    stock.put("CINTA-T200", 3);
  }

  public double calcularDescuento(Pedido pedido) {
    double descuento = pedido.cliente().tipo().descuentoPorCantidad(pedido.cantidad());
    if (pedido.envio().urgente()) {
      descuento = Math.max(0, descuento - DESCUENTO_URGENTE);
    }
    return descuento;
  }

  public double calcularCostoEnvio(Pedido pedido) {
    return pedido.envio().zona().costoEnvio(pedido.envio(), pedido.cantidad());
  }

  public double calcularTotal(Pedido pedido) {
    double subtotal = pedido.montoBase() * pedido.cantidad();
    double conDescuento = subtotal - subtotal * calcularDescuento(pedido);
    double conEnvio = conDescuento + calcularCostoEnvio(pedido);
    double total = conEnvio + conEnvio * IVA;
    return Math.round(total * 100.0) / 100.0;
  }

  public boolean hayStock(String sku, int cantidad) {
    return stock.containsKey(sku) && stock.get(sku) >= cantidad;
  }

  public void descontarStock(String sku, int cantidad) {
    Integer disponible = stock.get(sku);
    if (disponible == null) {
      throw new IllegalStateException("SKU inexistente " + sku);
    }
    if (disponible < cantidad) {
      throw new IllegalStateException("Stock insuficiente para " + sku);
    }
    stock.put(sku, disponible - cantidad);
  }
}
