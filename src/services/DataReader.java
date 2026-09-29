package services;

import models.Producto;
import models.Vendedor;
import models.Venta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataReader {
    private static final String DELIMITER = ";";
    private List<Producto> productos;
    private List<Vendedor> vendedores;
    private List<Venta> ventas;

    public DataReader() {
        this.productos = new ArrayList<>();
        this.vendedores = new ArrayList<>();
        this.ventas = new ArrayList<>();
    }

    /**
     * Lee todos los archivos de datos generados por GenerateInfoFiles
     * @return true si la lectura fue exitosa, false si hubo errores
     */
    public boolean leerTodosDatos() {
        try {
            leerProductos();
            leerVendedores();
            leerVentas();
            return true;
        } catch (IOException e) {
            System.err.println("Error al leer datos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lee el archivo productos.txt
     */
    private void leerProductos() throws IOException {
        String ruta = "productos.txt";
        if (!Files.exists(Paths.get(ruta))) {
            throw new FileNotFoundException("Archivo no encontrado: " + ruta);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(DELIMITER);
                if (partes.length >= 3) {
                    Producto p = new Producto(
                        partes[0].trim(),
                        partes[1].trim(),
                        Double.parseDouble(partes[2].trim())
                    );
                    productos.add(p);
                }
            }
        }
    }

    /**
     * Lee el archivo vendedores.txt
     */
    private void leerVendedores() throws IOException {
        String ruta = "vendedores.txt";
        if (!Files.exists(Paths.get(ruta))) {
            throw new FileNotFoundException("Archivo no encontrado: " + ruta);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(DELIMITER);
                if (partes.length >= 4) {
                    Vendedor v = new Vendedor(
                        partes[0].trim(),
                        partes[1].trim(),
                        partes[2].trim(),
                        partes[3].trim()
                    );
                    vendedores.add(v);
                }
            }
        }
    }

    /**
     * Lee todos los archivos de ventas (ventas_*.txt)
     */
    private void leerVentas() throws IOException {
        File directorio = new File(".");
        File[] archivos = directorio.listFiles((dir, name) ->
            name.startsWith("ventas_") && name.endsWith(".txt")
        );

        if (archivos == null || archivos.length == 0) {
            System.out.println("Advertencia: No se encontraron archivos de ventas");
            return;
        }

        for (File archivo : archivos) {
            leerArchivoVentas(archivo);
        }
    }

    /**
     * Lee un archivo individual de ventas
     */
    private void leerArchivoVentas(File archivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String primerLinea = reader.readLine();
            if (primerLinea == null || primerLinea.trim().isEmpty()) return;

            // Primera línea contiene datos del vendedor
            String[] datosVendedor = primerLinea.split(DELIMITER);
            Vendedor vendedor = buscarVendedor(datosVendedor[0].trim(), datosVendedor[1].trim());

            if (vendedor == null) {
                System.err.println("Vendedor no encontrado en: " + archivo.getName());
                return;
            }

            // Leer las ventas
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(DELIMITER);
                if (partes.length >= 2) {
                    Venta v = new Venta(
                        partes[0].trim(),
                        Integer.parseInt(partes[1].trim()),
                        vendedor
                    );
                    v.setProducto(buscarProducto(partes[0].trim()));
                    ventas.add(v);
                }
            }
        }
    }

    /**
     * Busca un vendedor por tipo y número de documento
     */
    private Vendedor buscarVendedor(String tipo, String numero) {
        for (Vendedor v : vendedores) {
            if (v.getTipoDocumento().equals(tipo) && v.getNumeroDocumento().equals(numero)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Busca un producto por ID
     */
    private Producto buscarProducto(String idProducto) {
        for (Producto p : productos) {
            if (p.getId().equals(idProducto)) {
                return p;
            }
        }
        return null;
    }

    // Getters
    public List<Producto> getProductos() {
        return productos;
    }

    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public List<Venta> getVentas() {
        return ventas;
    }
}
