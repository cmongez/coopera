package logic;

import java.sql.*;
import db.ConexionOracle;

public class ProcesarPagos {

    public static void ejecutarProcesoPagos() {
        System.out.println("🔄 === INICIANDO PROCESO DE PAGOS MENSUAL ===");
        
        try (Connection conn = ConexionOracle.getConnection()) {
            
            // 1. Obtener estadísticas ANTES del proceso
            int creditosAntes = obtenerTotalCreditos(conn);
            int pagosAntes = obtenerTotalPagos(conn);
            int erroresAntes = obtenerTotalErrores(conn);
            
            System.out.println("Estado inicial:");
            System.out.println("Créditos activos: " + creditosAntes);
            System.out.println("Pagos previos: " + pagosAntes);
            System.out.println("Errores previos: " + erroresAntes);
            
            // 2. Ejecutar el proceso principal
            System.out.println("\nProcesando pagos...");
            long inicioTiempo = System.currentTimeMillis();
            
            CallableStatement cs = conn.prepareCall("{call PKG_CREDITOS.ejecutar_proceso_pagos}");
            cs.execute();
            
            long tiempoTranscurrido = System.currentTimeMillis() - inicioTiempo;
            
            // 3. Obtener estadísticas DESPUÉS del proceso
            int creditosDespues = obtenerTotalCreditos(conn);
            int pagosDespues = obtenerTotalPagos(conn);
            int erroresDespues = obtenerTotalErrores(conn);
            
            // 4. Calcular diferencias
            int pagosNuevos = pagosDespues - pagosAntes;
            int erroresNuevos = erroresDespues - erroresAntes;
            
            // 5. Mostrar resultados detallados
            System.out.println("\n=== PROCESO COMPLETADO ===");
            System.out.println("Tiempo: " + tiempoTranscurrido + " ms");
            System.out.println("Resultados:");
            System.out.println("Créditos procesados: " + creditosDespues);
            System.out.println("Nuevos pagos generados: " + pagosNuevos);

            if (erroresNuevos > 0) {
                System.out.println("Nuevos errores: " + erroresNuevos);
                System.out.println("Consulte los reportes para ver detalles de errores");
            } else {
                System.out.println("Sin errores - Todos los créditos procesados correctamente");
            }
            
            // 6. Obtener información del último proceso de auditoría
            mostrarUltimaAuditoria(conn);
            
            System.out.println("\nProceso de pagos finalizado exitosamente.");
            
        } catch (SQLException e) {
            System.err.println("\nError al ejecutar proceso de pagos:");
            System.err.println("Código: " + e.getErrorCode());
            System.err.println("Mensaje: " + e.getMessage());
            
            if (e.getMessage().contains("ORA-")) {
                System.out.println("\nPosibles causas:");
                System.out.println("   • Verificar que las tablas tengan datos");
                System.out.println("   • Verificar que las secuencias existan");
                System.out.println("   • Verificar permisos en la base de datos");
            }
        }
    }
    
    /**
     * Obtiene el total de créditos activos
     */
    private static int obtenerTotalCreditos(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM CREDITO_SOCIO";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Obtiene el total de pagos procesados
     */
    private static int obtenerTotalPagos(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM PAGO_MENSUAL_CREDITO";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Obtiene el total de errores registrados
     */
    private static int obtenerTotalErrores(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM ERROR_PAGO_MENSUAL_CREDITO";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Muestra información de la última auditoría
     */
    private static void mostrarUltimaAuditoria(Connection conn) throws SQLException {
        String query = """
            SELECT fecha_ejecucion, usuario_ejecucion, estado 
            FROM AUDITORIA_PROCESOS 
            WHERE nombre_proceso = 'procesar_pagos_credito'
            AND fecha_ejecucion = (
                SELECT MAX(fecha_ejecucion) FROM AUDITORIA_PROCESOS 
                WHERE nombre_proceso = 'procesar_pagos_credito'
            )
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fecha_ejecucion");
                String usuario = rs.getString("usuario_ejecucion");
                String estado = rs.getString("estado");
                
                System.out.println("\nAuditoría:");
                System.out.println("Fecha: " + fecha);
                System.out.println("Usuario: " + usuario);
                System.out.println("Estado: " + estado);
            }
        } catch (SQLException e) {
            // No es crítico si falla la auditoría
            System.out.println("Información de auditoría no disponible");
        }
    }
}
