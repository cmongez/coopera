package dao;

import db.ConexionOracle;
import java.sql.*;

public class AuditoriaDAO {

    public void listarAuditoria() {
        System.out.println("\n=== REGISTRO DE AUDITORÍA DE PROCESOS ===");

        String sql = "SELECT id_auditoria, nombre_proceso, fecha_ejecucion, usuario_ejecucion, estado " +
                "FROM AUDITORIA_PROCESOS ORDER BY fecha_ejecucion DESC";

        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.printf("%-5s | %-25s | %-20s | %-15s | %-30s%n",
                    "ID", "PROCESO", "FECHA", "USUARIO", "ESTADO");
            System.out.println("----------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id_auditoria");
                String proceso = rs.getString("nombre_proceso");
                Timestamp fecha = rs.getTimestamp("fecha_ejecucion");
                String usuario = rs.getString("usuario_ejecucion");
                String estado = rs.getString("estado");

                // Si el estado es muy largo, lo truncamos
                if (estado != null && estado.length() > 28) {
                    estado = estado.substring(0, 28) + "...";
                }

                System.out.printf("%-5d | %-25s | %-20s | %-15s | %-30s%n",
                        id, proceso, fecha, usuario, estado);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar auditoría: " + e.getMessage());
        }
    }
}
