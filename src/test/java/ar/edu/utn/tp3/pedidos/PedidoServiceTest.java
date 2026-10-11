package ar.edu.utn.tp3.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PedidoServiceTest {

  private static final double DELTA = 0.0001;

  private PedidoService service;

  @BeforeEach
  void setUp() {
    service = new PedidoService();
  }

  private static Pedido pedido(
      TipoCliente tipo,
      Zona zona,
      double pesoKg,
      double montoBase,
      int cantidad,
      boolean urgente,
      boolean fragil) {
    return new Pedido(
        new Cliente("Cliente de prueba", tipo),
        new Envio(zona, pesoKg, urgente, fragil),
        montoBase,
        cantidad);
  }

  // ---------- Descuentos ----------

  @Test
  void descuentoMayoristaSegunTramo() {
    assertEquals(0.20, descuento(TipoCliente.MAYORISTA, 100, false), DELTA);
    assertEquals(0.15, descuento(TipoCliente.MAYORISTA, 60, false), DELTA);
    assertEquals(0.10, descuento(TipoCliente.MAYORISTA, 10, false), DELTA);
    assertEquals(0.05, descuento(TipoCliente.MAYORISTA, 9, false), DELTA);
  }

  @Test
  void descuentoVipSegunTramo() {
    assertEquals(0.25, descuento(TipoCliente.VIP, 100, false), DELTA);
    assertEquals(0.18, descuento(TipoCliente.VIP, 50, false), DELTA);
    assertEquals(0.12, descuento(TipoCliente.VIP, 49, false), DELTA);
  }

  @Test
  void descuentoMinoristaSegunTramo() {
    assertEquals(0.05, descuento(TipoCliente.MINORISTA, 10, false), DELTA);
    assertEquals(0.0, descuento(TipoCliente.MINORISTA, 9, false), DELTA);
  }

  @Test
  void pedidoUrgenteRestaDosPuntosDeDescuento() {
    assertEquals(0.13, descuento(TipoCliente.MAYORISTA, 60, true), DELTA);
  }

  @Test
  void descuentoUrgenteNuncaEsNegativo() {
    assertEquals(0.0, descuento(TipoCliente.MINORISTA, 2, true), DELTA);
  }

  private double descuento(TipoCliente tipo, int cantidad, boolean urgente) {
    return service.calcularDescuento(pedido(tipo, Zona.CABA, 3, 1000, cantidad, urgente, false));
  }

  // ---------- Envío ----------

  @Test
  void envioCabaSegunPeso() {
    assertEquals(1500.0, envio(Zona.CABA, 3, 1, false, false), DELTA);
    assertEquals(2500.0, envio(Zona.CABA, 12, 1, false, false), DELTA);
    assertEquals(4000.0, envio(Zona.CABA, 30, 1, false, false), DELTA);
  }

  @Test
  void envioCabaUrgenteTieneRecargoDel50Porciento() {
    assertEquals(2250.0, envio(Zona.CABA, 3, 1, true, false), DELTA);
  }

  @Test
  void envioCabaIgnoraFragilidadYVolumen() {
    assertEquals(1500.0, envio(Zona.CABA, 3, 100, false, true), DELTA);
  }

  @Test
  void envioGbaSegunPeso() {
    assertEquals(2200.0, envio(Zona.GBA, 3, 1, false, false), DELTA);
    assertEquals(3600.0, envio(Zona.GBA, 12, 1, false, false), DELTA);
    assertEquals(5500.0, envio(Zona.GBA, 30, 1, false, false), DELTA);
  }

  @Test
  void envioGbaSumaRecargoUrgenteYFragil() {
    assertEquals(3520.0, envio(Zona.GBA, 3, 1, true, false), DELTA);
    assertEquals(3000.0, envio(Zona.GBA, 3, 1, false, true), DELTA);
    assertEquals(4320.0, envio(Zona.GBA, 3, 1, true, true), DELTA);
  }

  @Test
  void envioInteriorSegunPeso() {
    assertEquals(3500.0, envio(Zona.INTERIOR, 3, 1, false, false), DELTA);
    assertEquals(6000.0, envio(Zona.INTERIOR, 12, 1, false, false), DELTA);
    assertEquals(9500.0, envio(Zona.INTERIOR, 30, 1, false, false), DELTA);
  }

  @Test
  void envioInteriorSumaTodosLosRecargos() {
    assertEquals(7000.0, envio(Zona.INTERIOR, 3, 1, true, false), DELTA);
    assertEquals(5000.0, envio(Zona.INTERIOR, 3, 1, false, true), DELTA);
    assertEquals(5500.0, envio(Zona.INTERIOR, 3, 51, false, false), DELTA);
    assertEquals(10500.0, envio(Zona.INTERIOR, 3, 60, true, true), DELTA);
  }

  private double envio(Zona zona, double pesoKg, int cantidad, boolean urgente, boolean fragil) {
    return service.calcularCostoEnvio(
        pedido(TipoCliente.MINORISTA, zona, pesoKg, 1000, cantidad, urgente, fragil));
  }

  // ---------- Total ----------

  @Test
  void totalMayoristaIncluyeDescuentoEnvioEIva() {
    Pedido p = pedido(TipoCliente.MAYORISTA, Zona.CABA, 12, 1000, 60, false, false);
    assertEquals(64735.0, service.calcularTotal(p), 0.01);
  }

  @Test
  void totalMinoristaUrgenteIncluyeEnvioEIva() {
    Pedido p = pedido(TipoCliente.MINORISTA, Zona.CABA, 3, 5000, 2, true, false);
    assertEquals(14822.5, service.calcularTotal(p), 0.01);
  }

  @Test
  void totalVipAlInteriorIncluyeRecargoPorVolumen() {
    Pedido p = pedido(TipoCliente.VIP, Zona.INTERIOR, 12, 2000, 100, false, false);
    assertEquals(191180.0, service.calcularTotal(p), 0.01);
  }

  // ---------- Stock ----------

  @Test
  void hayStockCuandoLaCantidadAlcanza() {
    assertTrue(service.hayStock("MANCUERNA-10", 50));
  }

  @Test
  void noHayStockCuandoFaltaOElSkuNoExiste() {
    assertFalse(service.hayStock("CINTA-T200", 4));
    assertFalse(service.hayStock("NO-EXISTE", 1));
  }

  @Test
  void descontarStockReduceLaExistencia() {
    service.descontarStock("BANCO-PLANO", 8);
    assertFalse(service.hayStock("BANCO-PLANO", 1));
  }

  @Test
  void descontarStockFallaSiNoAlcanza() {
    IllegalStateException error =
        assertThrows(IllegalStateException.class, () -> service.descontarStock("CINTA-T200", 4));
    assertEquals("Stock insuficiente para CINTA-T200", error.getMessage());
  }

  @Test
  void descontarStockFallaSiElSkuNoExiste() {
    IllegalStateException error =
        assertThrows(IllegalStateException.class, () -> service.descontarStock("NO-EXISTE", 1));
    assertEquals("SKU inexistente NO-EXISTE", error.getMessage());
  }
}
