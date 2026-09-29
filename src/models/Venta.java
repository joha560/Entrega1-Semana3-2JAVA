package models;

public class Venta {
    private String idProducto;
    private int cantidad;
    private Vendedor vendedor;
    private Producto producto;

    public Venta(String idProducto, int cantidad, Vendedor vendedor) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.vendedor = vendedor;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getTotalVenta() {
        if (producto != null) {
            return producto.getPrecio() * cantidad;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idProducto='" + idProducto + '\'' +
                ", cantidad=" + cantidad +
                ", vendedor=" + vendedor.getNombreCompleto() +
                ", totalVenta=" + getTotalVenta() +
                '}';
    }
}
