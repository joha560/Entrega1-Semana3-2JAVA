# Sistema de Generación de Reportes de Ventas

**Entrega 2 - Versión Preliminar**

## 📖 Descripción del Proyecto

Sistema Java que genera archivos planos con datos de ventas y crea reportes analíticos basados en esos datos. El proyecto está dividido en dos fases principales:

1. **Generación de Datos** (GenerateInfoFiles)
2. **Lectura y Generación de Reportes** (Main)

---

## 🗂️ Estructura del Proyecto

```
proyecto/
├── src/
│   ├── GenerateInfoFiles.java          # Genera archivos planos
│   ├── Main.java                       # Programa principal
│   ├── models/
│   │   ├── Producto.java              # Modelo de producto
│   │   ├── Vendedor.java              # Modelo de vendedor
│   │   └── Venta.java                 # Modelo de transacción de venta
│   └── services/
│       ├── DataReader.java            # Lee archivos planos
│       └── ReportGenerator.java       # Genera reportes
├── ElementosFaltantes.md               # Especificación de lo faltante
├── README.md                           # Este archivo
└── .classpath, .project, .settings/    # Configuración de Eclipse
```

---

## 🚀 Compilación y Ejecución

### Requisitos
- **Java 8 o superior**
- **Eclipse IDE** (recomendado) o compilador javac desde línea de comandos

### Opción 1: Desde Eclipse IDE

1. **Proyecto ya abierto en Eclipse**
   - El proyecto está importado como proyecto existente

2. **Compilar**
   - Click derecho en proyecto → Build Project
   - O: Project → Build All

3. **Ejecutar GenerateInfoFiles** (genera datos)
   - Click derecho en `src/GenerateInfoFiles.java`
   - Run As → Java Application
   - Se crearán archivos: `productos.txt`, `vendedores.txt`, `ventas_*.txt`

4. **Ejecutar Main** (genera reportes)
   - Click derecho en `src/Main.java`
   - Run As → Java Application
   - Se crearán archivos: `reporte_*.txt`

### Opción 2: Desde Línea de Comandos

```bash
# Navegar a la carpeta src
cd ruta/del/proyecto/src

# Compilar todas las clases
javac GenerateInfoFiles.java Main.java models/*.java services/*.java

# Ejecutar GenerateInfoFiles (generar datos)
java GenerateInfoFiles

# Ejecutar Main (generar reportes)
java Main
```

---

## 📊 Flujo de Ejecución

### Paso 1: Generar Datos
```
$ java GenerateInfoFiles
Generación finalizada correctamente en: [ruta]
```

**Archivos generados:**
- `productos.txt` - Formato: `id;nombre;precio`
- `vendedores.txt` - Formato: `tipo_doc;numero;nombre;apellido`
- `ventas_*.txt` - Formato: primera línea = vendedor, siguientes = venta

### Paso 2: Generar Reportes
```
$ java Main
===========================================
     SISTEMA DE REPORTES DE VENTAS
===========================================

Paso 1: Generando datos de entrada...
✓ Datos generados exitosamente

Paso 2: Leyendo datos del sistema...
✓ Datos cargados exitosamente
  - Productos: 8
  - Vendedores: 4
  - Transacciones de venta: 15

Paso 3: Generando reportes...
✓ Reporte de ventas por vendedor generado: reporte_ventas_por_vendedor.txt
✓ Reporte de productos más vendidos generado: reporte_productos_mas_vendidos.txt

===========================================
✓ PROCESO COMPLETADO EXITOSAMENTE
===========================================
```

---

## 📄 Reportes Generados

### Reporte 1: Ventas Totales por Vendedor
**Archivo:** `reporte_ventas_por_vendedor.txt`

```
===== REPORTE: VENTAS TOTALES POR VENDEDOR =====
Fecha de generación: [fecha]
=============================================

Ana García: $15,500.00
Carlos López: $12,200.50
Laura Martínez: $8,600.00
Miguel Rodríguez: $6,300.75

=============================================
TOTAL GENERAL: $42,601.25
```

### Reporte 2: Productos Más Vendidos
**Archivo:** `reporte_productos_mas_vendidos.txt`

```
===== REPORTE: PRODUCTOS MÁS VENDIDOS =====
Fecha de generación: [fecha]
==========================================

Cuaderno: 8 unidades
Lapiz: 6 unidades
Boligrafo: 5 unidades
Carpeta: 3 unidades

==========================================
TOTAL DE UNIDADES VENDIDAS: 22
```

---

## 🏗️ Arquitectura del Código

### Modelos (package `models`)

#### Producto.java
```java
public class Producto {
    private String id;
    private String nombre;
    private double precio;
    
    // Getters, setters, toString()
}
```

#### Vendedor.java
```java
public class Vendedor {
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    
    public String getNombreCompleto() { /* retorna nombre + apellido */ }
}
```

#### Venta.java
```java
public class Venta {
    private String idProducto;
    private int cantidad;
    private Vendedor vendedor;
    private Producto producto;
    
    public double getTotalVenta() { 
        return producto.getPrecio() * cantidad;
    }
}
```

### Servicios (package `services`)

#### DataReader.java
```java
public class DataReader {
    public boolean leerTodosDatos()     // Lee TODOS los archivos
    private void leerProductos()        // Lee productos.txt
    private void leerVendedores()       // Lee vendedores.txt
    private void leerVentas()           // Lee todos los ventas_*.txt
}
```

#### ReportGenerator.java
```java
public class ReportGenerator {
    public boolean generarReportes()    // Genera todos los reportes
    private void generarReporte...()    // Genera cada reporte
}
```

---

## ⚙️ Flujo de Datos

```
┌──────────────────────────────────────────┐
│    GenerateInfoFiles.main()              │
│  (Genera archivos planos pseudoaleatorios)│
└────────────────┬─────────────────────────┘
                 │
        ┌────────▼─────────┐
        │ productos.txt    │
        │ vendedores.txt   │
        │ ventas_*.txt     │
        └────────┬─────────┘
                 │
┌────────────────▼─────────────────────────┐
│    DataReader.leerTodosDatos()           │
│      (Lee y parsea archivos)             │
└────────────────┬─────────────────────────┘
                 │
      ┌──────────▼──────────┐
      │ List<Producto>      │
      │ List<Vendedor>      │
      │ List<Venta>         │
      └──────────┬──────────┘
                 │
┌────────────────▼─────────────────────────┐
│  ReportGenerator.generarReportes()       │
│      (Genera archivos de reportes)       │
└────────────────┬─────────────────────────┘
                 │
      ┌──────────▼───────────────────┐
      │ reporte_ventas_por_vendedor  │
      │ reporte_productos_vendidos   │
      │ [reportes 3 y 4 pendientes]  │
      └──────────────────────────────┘
```

---

## 🐛 Manejo de Errores

El programa maneja automáticamente:

| Situación | Comportamiento |
|-----------|----------------|
| Archivo no encontrado | `FileNotFoundException` → Mensaje de error |
| Archivo corrupto/malformado | Salta línea y continúa procesando |
| Vendedor no encontrado en ventas | Aviso y continúa |
| Producto no encontrado en ventas | Aviso y continúa |
| Error de I/O general | `IOException` → Mensaje de error |

---

## 📝 Especificación de Datos

### Formato: productos.txt
```
id;nombre;precio
1;Cuaderno;5000
2;Lapiz;2000
3;Boligrafo;3000
```

### Formato: vendedores.txt
```
tipo;numero;nombre;apellido
CC;10000001;Ana;García
CC;10000002;Carlos;López
CC;10000003;Laura;Martínez
CC;10000004;Miguel;Rodríguez
```

### Formato: ventas_[tipo]_[numero]_[nombre].txt
```
CC;10000001
1;5
2;10
3;2
```

---

## ✨ Características Implementadas

✅ Generación de datos pseudoaleatorios  
✅ Lectura de archivos planos con delimitador `;`  
✅ Modelos de datos (Producto, Vendedor, Venta)  
✅ Cálculo automático de totales de venta  
✅ Reportes ordenados (descendente por monto)  
✅ Manejo de excepciones  
✅ Mensajes informativos de progreso  
✅ **Sin solicitar entrada del usuario**  

---

## ⚠️ Lo que Falta (Entrega 3)

❌ Reportes 3 y 4 (pendiente especificación)  
❌ Pruebas unitarias (JUnit)  
❌ Validaciones extendidas  
❌ Archivo `conclusion.txt`  

Ver `ElementosFaltantes.md` para detalles completos.

---

## 🔧 Cómo Extender el Proyecto

### Agregar un Nuevo Reporte

1. Crear método en `ReportGenerator.java`:
```java
private void generarReporte5() throws IOException {
    // Tu lógica de reporte
}
```

2. Llamarlo desde `generarReportes()`:
```java
public boolean generarReportes() {
    try {
        generarReporteVentasPorVendedor();
        generarReporteProductosMasVendidos();
        generarReporte5();  // Nuevo
        return true;
    } catch (IOException e) { /* ... */ }
}
```

### Agregar Validación

En `DataReader.java` dentro de `leerProductos()`:
```java
if (Double.parseDouble(partes[2].trim()) < 0) {
    throw new IllegalArgumentException("Precio no puede ser negativo");
}
```

---

## 📧 Contacto y Preguntas

Para dudas sobre la arquitectura o implementación, revisar:
- Comentarios en el código
- `ElementosFaltantes.md` (especificaciones pendientes)
- Documentación en métodos individuales

---

*Versión Preliminar - Entrega 2  
Fecha: 2026-09-28  
Estado: Listo para revisar*
