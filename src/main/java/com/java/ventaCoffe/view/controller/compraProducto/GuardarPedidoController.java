package com.java.ventaCoffe.view.controller.compraProducto;

import com.java.ventaCoffe.controller.impl.*;
import com.java.ventaCoffe.model.entity.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GuardarPedidoController {

    private final Logger logger = LoggerFactory.getLogger(GuardarPedidoController.class);

    @Autowired
    private PedidoServiceImpl pedidoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private PedidoTempServiceImpl pedidoTempService;

    @Autowired
    private DetallePedidoServiceImpl detallePedidoService;

    @Autowired
    private ProductoServiceImpl productoService;

    public void guardarPedido(Double totalPedido, String usuario, TextField montoUsuario, Label totalVenta, Label totalCambio) {
        Optional<Usuario> user = usuarioService.findByCorreoUsuario(usuario);

        try {
            if (!user.isPresent()) {
                logger.info("El usuario no existe: {}", usuario);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de compra", "El usuario no existe.");
                return;
            }

            logger.info("El usuario existe: {}", user.get().getPreguntaUsuario());

            if (totalPedido <= 0.0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de compra", "Debe ingresar un monto válido para el pedido.");
                logger.info("No acepta valores 0");
                return;
            }

            if (montoUsuario.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de compra", "Debe ingresar el monto a pagar.");
                return;
            }

            Double prueba;
            try {
                prueba = Double.parseDouble(montoUsuario.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de compra", "El monto ingresado no es un número válido.");
                return;
            }

            if (prueba < totalPedido) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de compra", "El monto ingresado es menor que el total del pedido.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Confirmación de compra");
            alert.setContentText("¿Desea comprar los productos?");
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                Pedido pedido = new Pedido();
                pedido.setFechaPedido(LocalDateTime.now());
                pedido.setNumeroPedido(pedidoService.generarNumeroPedido());
                pedido.setTotalPedido(totalPedido);
                pedido.setUsuario(user.get());
                pedidoService.guardarPedido(pedido);
                mostrarAlerta(Alert.AlertType.INFORMATION,"Estado de la compra","Se realizo de manera correcta su pago, Muchas gracias");
                montoUsuario.setEditable(true);
                montoUsuario.setText("");
                totalVenta.setText("$0.0");
                totalCambio.setText("$0.0");
                guardarDetallePedido(pedido);
                pedidoTempService.eliminarRegistroTablaPedido();

            }

        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType alertType, String titulo, String contenido) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void guardarDetallePedido(Pedido pedido) {
        logger.info("Entrando al detalle del pedido");
        List<PedidoTemporal> pedidoTemporals = pedidoTempService.mostrarPedidos();
        for (PedidoTemporal pedidoTemporal : pedidoTemporals) {
            logger.info("Entrando al foreach");
            DetallePedido detallePedido = new DetallePedido(); // Crear un nuevo objeto en cada iteración
            System.out.println(pedidoTemporal);
            detallePedido.setCantidadDetallePedido(pedidoTemporal.getCantidadPedidoTemp());
            detallePedido.setNombreDetallePedido(pedidoTemporal.getNombrePedidoTemp());
            detallePedido.setTotalDetallePedido(pedidoTemporal.getPrecioPedidoTemp());
            detallePedido.setPedido(pedido);
            Optional<Producto> producto=productoService.findByNombreProducto(pedidoTemporal.getNombrePedidoTemp());
            detallePedido.setProducto(producto.get());
            detallePedidoService.guardarPedido(detallePedido);
        }
        pedidoTemporals.clear();
    }


}
