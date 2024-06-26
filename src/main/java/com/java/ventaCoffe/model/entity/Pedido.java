package com.java.ventaCoffe.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    private String numeroPedido;

    private LocalDateTime fechaPedido;

    private double totalPedido;

    public Pedido(Integer idPedido, String numeroPedido, LocalDateTime fechaPedido, double totalPedido) {
        this.idPedido = idPedido;
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.totalPedido = totalPedido;
    }

    @ManyToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido")
    private List<DetallePedido> detallePedido;


}
