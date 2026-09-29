package services;

import models.Producto;
import models.Vendedor;
import models.Venta;

import java.io.*;
import java.util.*;

public class ReportGenerator {
    private List<Producto> productos;
    private List<Vendedor> vendedores;
    private List<Venta> ventas;

    public ReportGenerator(List<Producto> productos, List<Vendedor> vendedores, List<Venta> ventas) {
        this.productos = productos;
        this.vendedores = vendedores;
        this.ventas = ventas;
    }

    /**
     * Genera todos los reportes solicitados
     * NOTA: La especificación de los reportes (puntos 3 y 4) se completará en la entrega final
     * @return true si se generaron exitosamente, false en caso de error
     */
    public boolean generarReportes() {
        try {
            generarReporteVentasPorVendedor();
            generarReporteProductosMasVendidos();
            // Aquí se agregarán más reportes según puntos 3 y 4
            return true;
        } catch (IOException e) {
            System.err.println("Error al generar reportes: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método auxiliar compatible con Java 8 para repetir strings
     */
    private String repetir(String str, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * REPORTE 1 (PRELIMINAR): Ventas totales por vendedor
     * Muestra cada vendedor con sus ventas totales en pesos
     */
    private void generarReporteVentasPorVendedor() throws IOException {
        Map<String, Double> ventasPorVendedor = new HashMap<>();

        for (Venta venta : ventas) {
            String nombreVendedor = venta.getVendedor().getNombreCompleto();
            double totalVenta = venta.getTotalVenta();
            ventasPorVendedor.put(nombreVendedor,
                ventasPorVendedor.getOrDefault(nombreVendedor, 0.0) + totalVenta);
        }

        try (FileWriter writer = new FileWriter("reporte_ventas_por_vendedor.txt")) {
            writer.write("===== REPORTE: VENTAS TOTALES POR VENDEDOR =====\n");
            writer.write("Fecha de generación: " + new Date() + "\n");
            writer.write("=============================================\n\n");

            ventasPorVendedor.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    try {
                        writer.write(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            double totalVentas = ventasPorVendedor.values().stream()
                .mapToDouble(Double::doubleValue).sum();
            writer.write("\n" + repetir("=", 45) + "\n");
            writer.write(String.format("TOTAL GENERAL: $%.2f\n", totalVentas));
        }

        System.out.println("✓ Reporte de ventas por vendedor generado: reporte_ventas_por_vendedor.txt");
    }

    /**
     * REPORTE 2 (PRELIMINAR): Productos más vendidos
     * Muestra productos ordenados por cantidad vendida
     */
    private void generarReporteProductosMasVendidos() throws IOException {
        Map<String, Integer> cantidadPorProducto = new HashMap<>();

        for (Venta venta : ventas) {
            String nombreProducto = venta.getProducto() != null ?
                venta.getProducto().getNombre() : "Producto desconocido";
            cantidadPorProducto.put(nombreProducto,
                cantidadPorProducto.getOrDefault(nombreProducto, 0) + venta.getCantidad());
        }

        try (FileWriter writer = new FileWriter("reporte_productos_mas_vendidos.txt")) {
            writer.write("===== REPORTE: PRODUCTOS MÁS VENDIDOS =====\n");
            writer.write("Fecha de generación: " + new Date() + "\n");
            writer.write("==========================================\n\n");

            cantidadPorProducto.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    try {
                        writer.write(String.format("%s: %d unidades\n", entry.getKey(), entry.getValue()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            int totalUnidades = cantidadPorProducto.values().stream()
                .mapToInt(Integer::intValue).sum();
            writer.write("\n" + repetir("=", 42) + "\n");
            writer.write(String.format("TOTAL DE UNIDADES VENDIDAS: %d\n", totalUnidades));
        }

        System.out.println("✓ Reporte de productos más vendidos generado: reporte_productos_mas_vendidos.txt");
    }

    // ============ STUBS PARA REPORTES FUTUROS ============
    // Estos métodos serán completados cuando se especifiquen los puntos 3 y 4

    /**
     * REPORTE 3 (A ESPECIFICAR)
     * TODO: Define según requisitos punto 3
     */
    private void generarReporte3() throws IOException {
        System.out.println("⚠ Reporte 3: Pendiente de especificación (punto 3 de requisitos)");
    }

    /**
     * REPORTE 4 (A ESPECIFICAR)
     * TODO: Define según requisitos punto 4
     */
    private void generarReporte4() throws IOException {
        System.out.println("⚠ Reporte 4: Pendiente de especificación (punto 4 de requisitos)");
    }
}
