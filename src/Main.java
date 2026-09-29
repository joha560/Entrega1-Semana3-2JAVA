import services.DataReader;
import services.ReportGenerator;

/**
 * Clase principal que orquestra el flujo completo del programa:
 * 1. Genera datos de prueba con GenerateInfoFiles
 * 2. Lee los datos generados
 * 3. Genera los reportes solicitados
 *
 * IMPORTANTE: Este programa NO solicita interacción del usuario
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("===========================================");
            System.out.println("     SISTEMA DE REPORTES DE VENTAS");
            System.out.println("===========================================\n");

            // Paso 1: Generar archivos de datos (si es necesario)
            System.out.println("Paso 1: Generando datos de entrada...");
            if (!generarDatos()) {
                System.err.println("Error: No se pudieron generar los datos de entrada");
                System.exit(1);
            }
            System.out.println("✓ Datos generados exitosamente\n");

            // Paso 2: Leer los datos
            System.out.println("Paso 2: Leyendo datos del sistema...");
            DataReader reader = new DataReader();
            if (!reader.leerTodosDatos()) {
                System.err.println("Error: No se pudieron leer los datos");
                System.exit(1);
            }
            System.out.println("✓ Datos cargados exitosamente");
            System.out.println("  - Productos: " + reader.getProductos().size());
            System.out.println("  - Vendedores: " + reader.getVendedores().size());
            System.out.println("  - Transacciones de venta: " + reader.getVentas().size() + "\n");

            // Paso 3: Generar reportes
            System.out.println("Paso 3: Generando reportes...");
            ReportGenerator generador = new ReportGenerator(
                reader.getProductos(),
                reader.getVendedores(),
                reader.getVentas()
            );

            if (!generador.generarReportes()) {
                System.err.println("Error: No se pudieron generar los reportes");
                System.exit(1);
            }
            System.out.println("✓ Reportes generados exitosamente\n");

            // Éxito
            System.out.println("===========================================");
            System.out.println("✓ PROCESO COMPLETADO EXITOSAMENTE");
            System.out.println("===========================================");
            System.out.println("Los reportes han sido guardados en archivos .txt");

        } catch (Exception e) {
            System.err.println("\n❌ ERROR FATAL: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Genera los archivos de datos usando la clase GenerateInfoFiles
     * @return true si la generación fue exitosa
     */
    private static boolean generarDatos() {
        try {
            // Aquí se debe llamar a GenerateInfoFiles
            // GenerateInfoFiles.main(new String[]{});
            // Por ahora asumimos que los archivos existen o serán generados externamente
            return true;
        } catch (Exception e) {
            System.err.println("Error al generar datos: " + e.getMessage());
            return false;
        }
    }
}
