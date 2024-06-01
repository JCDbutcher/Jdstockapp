package utils;
import entity.DetalleTransaccion;
import entity.Transaccion;

import java.math.BigDecimal;
import java.util.List;

public class TransactionEmailService {

    public static String generateInvoiceHtml(Transaccion transaccion, List<DetalleTransaccion> detalles) {
        StringBuilder html = new StringBuilder();
        html.append("<h1>Invoice</h1>");
        html.append("<h2>Transaction Details</h2>");
        html.append("<p>Transaction ID: ").append(transaccion.getId()).append("</p>");
        html.append("<p>Date: ").append(transaccion.getFechaPedido()).append("</p>");
        html.append("<p>Customer: ").append(transaccion.getCliente().getNombre()).append("</p>");
        html.append("<h2>Items</h2>");
        html.append("<table border='1'>");
        html.append("<tr><th>Item</th><th>Quantity</th><th>Price</th><th>Total</th></tr>");

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleTransaccion detalle : detalles) {
            BigDecimal itemTotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            total = total.add(itemTotal);
            html.append("<tr>")
                    .append("<td>").append(detalle.getProducto().getNombre()).append("</td>")
                    .append("<td>").append(detalle.getCantidad()).append("</td>")
                    .append("<td>").append(detalle.getPrecioUnitario()).append("</td>")
                    .append("<td>").append(itemTotal).append("</td>")
                    .append("</tr>");
        }
        html.append("</table>");
        html.append("<h2>Total: ").append(total).append("</h2>");

        return html.toString();
    }
}

