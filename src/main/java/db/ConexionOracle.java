package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase para conexión a Oracle Cloud usando Wallet (método más seguro)
 */
public class ConexionOracle {

    // Ruta al wallet descargado de Oracle Cloud
    private static final String WALLET_PATH = "C:/oracle/wallet"; // Cambia esta ruta por donde extraíste tu wallet

    // Nombre del servicio (viene en tnsnames.ora del wallet)
    private static final String SERVICE_NAME = "basedatosentrega2_high"; // _high para mejor rendimiento

    // Credenciales
    private static final String USER = "ADMIN";
    private static final String PASS = "";

    /**
     * Conexión usando Oracle Wallet (recomendado para Oracle Cloud)
     */
    public static Connection getConnectionWithWallet() throws SQLException {
        try {
            // Cargar driver
            Class.forName("oracle.jdbc.OracleDriver");

            // Configurar propiedades para usar wallet
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);

            // Configuración del wallet
            System.setProperty("oracle.net.tns_admin", WALLET_PATH);
            System.setProperty("oracle.net.ssl_version", "1.2");

            // URL usando el nombre del servicio del tnsnames.ora
            String url = "jdbc:oracle:thin:@" + SERVICE_NAME;

            Connection conn = DriverManager.getConnection(url, props);
            return conn;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle JDBC no encontrado: " + e.getMessage());
        }
    }

    /**
     * Método alternativo con URL completa del wallet
     */
    public static Connection getConnectionWithWalletURL() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            // URL que incluye la configuración del wallet directamente
            String url = "jdbc:oracle:thin:@" + SERVICE_NAME +
                    "?TNS_ADMIN=" + WALLET_PATH;

            Connection conn = DriverManager.getConnection(url, USER, PASS);
            System.out.println("Conexión establecida con Oracle Cloud (Wallet URL).");
            return conn;

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle JDBC no encontrado: " + e.getMessage());
        }
    }
    /**
     * Método simple para conexión (alias de getConnectionWithWallet)
     */
    public static Connection getConnection() throws SQLException {
        return getConnectionWithWallet();
    }
    
    /**
     * Método para probar la conexión con diagnósticos detallados
     */
    public static void testConnection() {
        System.out.println("=== DIAGNÓSTICO DE CONEXIÓN ORACLE ===");
        System.out.println("Wallet Path: " + WALLET_PATH);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("Usuario: " + USER);
        System.out.println("Password: " + (PASS.length() > 0 ? "*".repeat(PASS.length()) : "VACÍA"));
        
        try {
            System.out.println("\n Intentando conexión...");
            Connection conn = getConnection();
            System.out.println("  ¡Conexión exitosa!");
            
            // Probar una consulta simple
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT USER FROM DUAL");
            if (rs.next()) {
                System.out.println(" Usuario conectado: " + rs.getString(1));
            }
            
            conn.close();
            System.out.println("Conexión cerrada correctamente.");
            
        } catch (SQLException e) {
            System.err.println("   Error de conexión:");
            System.err.println("   Código: " + e.getErrorCode());
            System.err.println("   Mensaje: " + e.getMessage());
            
            if (e.getMessage().contains("username/password")) {
                System.err.println("\n  SUGERENCIAS:");
                System.err.println("   1. Verificar credenciales de usuario COOPERA");
                System.err.println("   2. Verificar que el usuario existe en la base de datos");
                System.err.println("   3. Verificar que el usuario tiene permisos de conexión");
            }
            
            if (e.getMessage().contains("wallet") || e.getMessage().contains("TNS")) {
                System.err.println("\n  SUGERENCIAS:");
                System.err.println("   1. Verificar que el wallet está en: " + WALLET_PATH);
                System.err.println("   2. Verificar que tnsnames.ora contiene: " + SERVICE_NAME);
                System.err.println("   3. Verificar permisos de lectura en archivos del wallet");
            }
        }
    }
}
