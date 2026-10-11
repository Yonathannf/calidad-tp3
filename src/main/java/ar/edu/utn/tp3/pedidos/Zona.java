package ar.edu.utn.tp3.pedidos;

/** Zona de entrega con sus tarifas y recargos de envío. */
public enum Zona {
  CABA(1500, 2500, 4000, 1.5, 0, 0),
  GBA(2200, 3600, 5500, 1.6, 800, 0),
  INTERIOR(3500, 6000, 9500, 2.0, 1500, 2000);

  private static final double LIMITE_LIVIANO_KG = 5;
  private static final double LIMITE_MEDIO_KG = 20;
  private static final int UMBRAL_VOLUMEN = 50;

  private final double tarifaLiviana;
  private final double tarifaMedia;
  private final double tarifaPesada;
  private final double multiplicadorUrgente;
  private final double recargoFragil;
  private final double recargoVolumen;

  Zona(
      double tarifaLiviana,
      double tarifaMedia,
      double tarifaPesada,
      double multiplicadorUrgente,
      double recargoFragil,
      double recargoVolumen) {
    this.tarifaLiviana = tarifaLiviana;
    this.tarifaMedia = tarifaMedia;
    this.tarifaPesada = tarifaPesada;
    this.multiplicadorUrgente = multiplicadorUrgente;
    this.recargoFragil = recargoFragil;
    this.recargoVolumen = recargoVolumen;
  }

  /** Costo de envío: tarifa por peso, más recargo urgente, frágil y por volumen. */
  public double costoEnvio(Envio envio, int cantidad) {
    double costo = tarifaBase(envio.pesoKg());
    if (envio.urgente()) {
      costo = costo * multiplicadorUrgente;
    }
    if (envio.fragil()) {
      costo = costo + recargoFragil;
    }
    if (cantidad > UMBRAL_VOLUMEN) {
      costo = costo + recargoVolumen;
    }
    return costo;
  }

  private double tarifaBase(double pesoKg) {
    if (pesoKg <= LIMITE_LIVIANO_KG) {
      return tarifaLiviana;
    }
    if (pesoKg <= LIMITE_MEDIO_KG) {
      return tarifaMedia;
    }
    return tarifaPesada;
  }
}
