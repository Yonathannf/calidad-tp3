package ar.edu.utn.tp3.pedidos;

/** Datos de entrega del pedido. */
public record Envio(Zona zona, double pesoKg, boolean urgente, boolean fragil) {}
