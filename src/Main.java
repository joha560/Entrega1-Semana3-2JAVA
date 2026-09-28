import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee los archivos generados por {@link GenerateInfoFiles}, calcula los
 * resultados de ventas y crea los reportes solicitados.
 */
public final class Main {

    private static final Path PROJECT_DIRECTORY = Paths.get(".");
    private static final Path PRODUCTS_FILE = PROJECT_DIRECTORY.resolve("productos.txt");
    private static final Path SALESMEN_FILE = PROJECT_DIRECTORY.resolve("vendedores.txt");
    private static final Path SALESMEN_REPORT = PROJECT_DIRECTORY.resolve("reporte_vendedores.csv");
    private static final Path PRODUCTS_REPORT = PROJECT_DIRECTORY.resolve("reporte_productos.csv");

    private Main() {
        // Clase de utilidad: todos sus métodos son estáticos.
    }

    /**
     * Ejecuta el procesamiento completo sin solicitar datos por teclado.
     *
     * @param args argumentos no utilizados
     */
    public static void main(String[] args) {
        try {
            Map<Integer, Product> products = readProducts(PRODUCTS_FILE);
            Map<String, Salesman> salesmen = readSalesmen(SALESMEN_FILE);
            int processedFiles = processSalesFiles(products, salesmen);

            if (processedFiles == 0) {
                throw new IllegalArgumentException("No se encontraron archivos de ventas.");
            }

            writeSalesmenReport(salesmen);
            writeProductsReport(products);

            System.out.println("Reportes generados correctamente:");
            System.out.println("- " + SALESMEN_REPORT.toAbsolutePath());
            System.out.println("- " + PRODUCTS_REPORT.toAbsolutePath());
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Error al procesar los archivos: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static Map<Integer, Product> readProducts(Path file) throws IOException {
        ensureRegularFile(file);
        Map<Integer, Product> products = new HashMap<Integer, Product>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(";", -1);
                if (fields.length != 3) {
                    throw formatError(file, lineNumber, "se esperaban 3 campos");
                }

                int id = parsePositiveInt(fields[0], file, lineNumber, "ID de producto");
                String name = requireText(fields[1], file, lineNumber, "nombre del producto");
                BigDecimal price = parsePositiveDecimal(fields[2], file, lineNumber, "precio");

                if (products.put(id, new Product(id, name, price)) != null) {
                    throw formatError(file, lineNumber, "ID de producto repetido: " + id);
                }
            }
        }

        if (products.isEmpty()) {
            throw new IllegalArgumentException("El archivo productos.txt está vacío.");
        }
        return products;
    }

    private static Map<String, Salesman> readSalesmen(Path file) throws IOException {
        ensureRegularFile(file);
        Map<String, Salesman> salesmen = new HashMap<String, Salesman>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(";", -1);
                if (fields.length != 4) {
                    throw formatError(file, lineNumber, "se esperaban 4 campos");
                }

                String documentType = requireText(fields[0], file, lineNumber, "tipo de documento");
                String documentNumber = requireText(fields[1], file, lineNumber, "número de documento");
                String firstName = requireText(fields[2], file, lineNumber, "nombre");
                String lastName = requireText(fields[3], file, lineNumber, "apellido");
                String key = salesmanKey(documentType, documentNumber);

                if (salesmen.put(key,
                        new Salesman(documentType, documentNumber, firstName, lastName)) != null) {
                    throw formatError(file, lineNumber, "documento de vendedor repetido");
                }
            }
        }

        if (salesmen.isEmpty()) {
            throw new IllegalArgumentException("El archivo vendedores.txt está vacío.");
        }
        return salesmen;
    }

    private static int processSalesFiles(Map<Integer, Product> products,
            Map<String, Salesman> salesmen) throws IOException {
        int processedFiles = 0;

        try (DirectoryStream<Path> files = Files.newDirectoryStream(
                PROJECT_DIRECTORY, "ventas_*.txt")) {
            for (Path file : files) {
                if (Files.isRegularFile(file)) {
                    processSalesFile(file, products, salesmen);
                    processedFiles++;
                }
            }
        }
        return processedFiles;
    }

    private static void processSalesFile(Path file, Map<Integer, Product> products,
            Map<String, Salesman> salesmen) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String identificationLine = reader.readLine();
            if (identificationLine == null) {
                throw new IllegalArgumentException("El archivo " + file.getFileName() + " está vacío.");
            }

            String[] identification = identificationLine.split(";", -1);
            if (identification.length != 2) {
                throw formatError(file, 1, "identificación de vendedor incorrecta");
            }

            String key = salesmanKey(
                    requireText(identification[0], file, 1, "tipo de documento"),
                    requireText(identification[1], file, 1, "número de documento"));
            Salesman salesman = salesmen.get(key);
            if (salesman == null) {
                throw formatError(file, 1, "el vendedor no existe en vendedores.txt");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(";", -1);
                if (fields.length != 3 || !fields[2].isEmpty()) {
                    throw formatError(file, lineNumber, "la venta debe tener el formato ID;cantidad;");
                }

                int productId = parsePositiveInt(fields[0], file, lineNumber, "ID de producto");
                int quantity = parsePositiveInt(fields[1], file, lineNumber, "cantidad");
                Product product = products.get(productId);
                if (product == null) {
                    throw formatError(file, lineNumber,
                            "el producto " + productId + " no existe en productos.txt");
                }

                product.soldQuantity += quantity;
                salesman.revenue = salesman.revenue.add(
                        product.price.multiply(BigDecimal.valueOf(quantity)));
            }
        }
    }

    private static void writeSalesmenReport(Map<String, Salesman> salesmen) throws IOException {
        List<Salesman> ordered = new ArrayList<Salesman>(salesmen.values());
        Collections.sort(ordered, new Comparator<Salesman>() {
            @Override
            public int compare(Salesman first, Salesman second) {
                int revenueComparison = second.revenue.compareTo(first.revenue);
                if (revenueComparison != 0) {
                    return revenueComparison;
                }
                return first.fullName().compareToIgnoreCase(second.fullName());
            }
        });

        try (BufferedWriter writer = Files.newBufferedWriter(SALESMEN_REPORT,
                StandardCharsets.UTF_8)) {
            for (Salesman salesman : ordered) {
                writer.write(salesman.fullName() + ";" + salesman.revenue.toPlainString());
                writer.newLine();
            }
        }
    }

    private static void writeProductsReport(Map<Integer, Product> products) throws IOException {
        List<Product> ordered = new ArrayList<Product>(products.values());
        Collections.sort(ordered, new Comparator<Product>() {
            @Override
            public int compare(Product first, Product second) {
                int quantityComparison = Integer.compare(second.soldQuantity, first.soldQuantity);
                if (quantityComparison != 0) {
                    return quantityComparison;
                }
                return first.name.compareToIgnoreCase(second.name);
            }
        });

        try (BufferedWriter writer = Files.newBufferedWriter(PRODUCTS_REPORT,
                StandardCharsets.UTF_8)) {
            for (Product product : ordered) {
                writer.write(product.name + ";" + product.price.toPlainString()
                        + ";" + product.soldQuantity);
                writer.newLine();
            }
        }
    }

    private static void ensureRegularFile(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("No se encontró el archivo " + file.getFileName() + ".");
        }
    }

    private static int parsePositiveInt(String value, Path file, int line, String field) {
        try {
            int number = Integer.parseInt(value.trim());
            if (number <= 0) {
                throw formatError(file, line, field + " debe ser mayor que cero");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw formatError(file, line, field + " no es un número entero válido");
        }
    }

    private static BigDecimal parsePositiveDecimal(String value, Path file, int line, String field) {
        try {
            BigDecimal number = new BigDecimal(value.trim());
            if (number.compareTo(BigDecimal.ZERO) <= 0) {
                throw formatError(file, line, field + " debe ser mayor que cero");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw formatError(file, line, field + " no es un número válido");
        }
    }

    private static String requireText(String value, Path file, int line, String field) {
        String text = value.trim();
        if (text.isEmpty()) {
            throw formatError(file, line, field + " no puede estar vacío");
        }
        return text;
    }

    private static String salesmanKey(String documentType, String documentNumber) {
        return documentType.trim().toUpperCase() + ";" + documentNumber.trim();
    }

    private static IllegalArgumentException formatError(Path file, int line, String detail) {
        return new IllegalArgumentException(file.getFileName() + ", línea " + line + ": " + detail + ".");
    }

    private static final class Product {
        private final int id;
        private final String name;
        private final BigDecimal price;
        private int soldQuantity;

        private Product(int id, String name, BigDecimal price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    private static final class Salesman {
        private final String documentType;
        private final String documentNumber;
        private final String firstName;
        private final String lastName;
        private BigDecimal revenue = BigDecimal.ZERO;

        private Salesman(String documentType, String documentNumber,
                String firstName, String lastName) {
            this.documentType = documentType;
            this.documentNumber = documentNumber;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        private String fullName() {
            return firstName + " " + lastName;
        }
    }
}
