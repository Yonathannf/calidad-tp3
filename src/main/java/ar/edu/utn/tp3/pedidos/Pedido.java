package ar.edu.utn.tp3.pedidos;

/** Pedido del depósito: quién compra, cómo se entrega, precio unitario y cantidad. */
public record Pedido(Cliente cliente, Envio envio, double montoBase, int cantidad) {}
