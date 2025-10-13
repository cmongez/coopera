package logic;
import java.sql.*;
import db.ConexionOracle;

/**
 * Clase utilitaria para generar reportes del sistema COOPERA
 * Utiliza los procedimientos almacenados de la base de datos Oracle
 */
public class Reportes {
    /**
     * 1. USAR procedimiento reporte_creditos_por_socio_cursor
     */
    public static void mostrarReporteCreditosSocios() {
        System.out.println("=== REPORTE CRÉDITOS POR SOCIO ===");

        try (Connection conn = ConexionOracle.getConnection()) {

            CallableStatement cs = conn.prepareCall("{call reporte_creditos_por_socio_cursor(?)}");
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);

            // Encabezados
            System.out.printf("%-8s | %-25s | %-8s | %-12s | %-10s | %-12s%n",
                    "SOCIO", "NOMBRE", "CRÉDITOS", "MONTO TOTAL", "SOLICITUDES", "RUN");

            String separador = "";
            for (int i = 0; i < 85; i++) separador += "─";
            System.out.println(separador);

            int totalSocios = 0;
            double granTotal = 0;
            int granTotalCreditos = 0;

            while (rs.next()) {
                int nroSocio = rs.getInt("NRO_SOCIO");
                String nombre = rs.getString("NOMBRE_COMPLETO");
                int creditos = rs.getInt("TOTAL_CREDITOS");
                double monto = rs.getDouble("MONTO_TOTAL_CREDITOS");
                int solicitudes = rs.getInt("SOLICITUDES_CREDITO");
                String run = rs.getString("RUN_SOCIO");

                // Truncar nombre si es muy largo
                if (nombre != null && nombre.length() > 25) {
                    nombre = nombre.substring(0, 22) + "...";
                }

                System.out.printf("%-8d | %-25s | %-8d | $%-11.2f | %-10d | %-12s%n",
                        nroSocio, nombre != null ? nombre : "Sin nombre",
                        creditos, monto, solicitudes, run != null ? run : "Sin RUN");

                totalSocios++;
                granTotal += monto;
                granTotalCreditos += creditos;
            }

            System.out.println(separador);
            System.out.printf("RESUMEN: %d socios | %d créditos | $%.2f total%n",
                    totalSocios, granTotalCreditos, granTotal);
            System.out.println("Datos obtenidos desde reporte_creditos_por_socio_cursor");

            rs.close();
            cs.close();

        } catch (SQLException e) {
            System.err.println("Error en reporte: " + e.getMessage());
            System.err.println("Código de error: " + e.getErrorCode());
            if (e.getMessage().contains("must be declared") || e.getMessage().contains("does not exist")) {
                System.out.println("El procedimiento reporte_creditos_por_socio_cursor no existe.");
                System.out.println("Ejecuta primero informe_mejorado.sql para crear el procedimiento.");
            }
        }
    }

    /**
     * 2. USA paquete PKG_CREDITOS para obtener estadísticas
     */
    public static void mostrarEstadisticasProceso() {
        System.out.println("\n=== ESTADÍSTICAS DEL PROCESO (PKG_CREDITOS) ===");

        try (Connection conn = ConexionOracle.getConnection()) {

            CallableStatement cs = conn.prepareCall("{call PKG_CREDITOS.get_estadisticas_proceso(?, ?, ?, ?, ?)}");
            cs.registerOutParameter(1, Types.INTEGER); // total_creditos
            cs.registerOutParameter(2, Types.INTEGER); // total_pagos
            cs.registerOutParameter(3, Types.INTEGER); // total_errores
            cs.registerOutParameter(4, Types.DATE);    // fecha_ultimo_proceso
            cs.registerOutParameter(5, Types.DOUBLE);  // monto_total
            cs.execute();

            int totalCreditos = cs.getInt(1);
            int totalPagos = cs.getInt(2);
            int totalErrores = cs.getInt(3);
            Date fechaUltimo = cs.getDate(4);
            double montoTotal = cs.getDouble(5);

            System.out.println("ESTADÍSTICAS DEL SISTEMA:");
            System.out.println("Total créditos en sistema: " + totalCreditos);
            System.out.println("Total pagos generados: " + totalPagos);
            System.out.println("Total errores registrados: " + totalErrores);
            System.out.println("Monto total de créditos: $" + String.format("%.2f", montoTotal));
            System.out.println("Último proceso exitoso: " +
                    (fechaUltimo != null ? fechaUltimo.toString() : "Nunca ejecutado"));

            // Calcular métricas adicionales
            if (totalCreditos > 0) {
                double promedioCredito = montoTotal / totalCreditos;
                System.out.println("Promedio por crédito: $" + String.format("%.2f", promedioCredito));

                double tasaError = totalErrores > 0 ? (double) totalErrores / totalCreditos * 100 : 0;
                System.out.println("Tasa de errores: " + String.format("%.2f%%", tasaError));
            }

            System.out.println("Estadísticas obtenidas desde PAQUETE PKG_CREDITOS");

            cs.close();

        } catch (SQLException e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
        }
    }

    /**
     * 3. USAR tu función get_detalle_creditos_socio
     */
    public static void mostrarDetalleSocio(int nroSocio) {
        System.out.println("\n=== DETALLE CRÉDITOS SOCIO " + nroSocio + " (FUNCIÓN) ===");

        try (Connection conn = ConexionOracle.getConnection()) {

            CallableStatement cs = conn.prepareCall("{? = call get_detalle_creditos_socio(?)}");
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setInt(2, nroSocio);
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);

            System.out.printf("%-12s | %-12s | %-6s | %-20s | %-7s | %-12s | %-10s%n",
                    "SOLICITUD", "MONTO", "CUOTAS", "TIPO CRÉDITO", "TASA %", "CUOTA MES", "ESTADO");

            String separador = "";
            for (int i = 0; i < 90; i++) separador += "─";
            System.out.println(separador);

            boolean tieneCreditos = false;
            String nombreSocio = "";
            String runSocio = "";
            double totalMonto = 0;
            int totalCreditos = 0;

            while (rs.next()) {
                tieneCreditos = true;

                int solicitud = rs.getInt("nro_solic_credito");
                double monto = rs.getDouble("monto_total_credito");
                int cuotas = rs.getInt("total_cuotas_credito");
                String tipoCredito = rs.getString("nombre_credito");
                double tasa = rs.getDouble("tasa_interes_anual");
                double cuotaMes = rs.getDouble("cuota_mensual");
                String estado = rs.getString("estado_credito");
                nombreSocio = rs.getString("nombre_socio");
                runSocio = rs.getString("run_socio");

                // Truncar tipo de crédito si es muy largo
                if (tipoCredito != null && tipoCredito.length() > 20) {
                    tipoCredito = tipoCredito.substring(0, 17) + "...";
                }

                System.out.printf("%-12d | $%-11.2f | %-6d | %-20s | %-7.2f | $%-11.2f | %-10s%n",
                        solicitud, monto, cuotas,
                        tipoCredito != null ? tipoCredito : "Sin tipo",
                        tasa, cuotaMes, estado);

                totalMonto += monto;
                totalCreditos++;
            }

            if (!tieneCreditos) {
                System.out.println("No se encontraron créditos para el socio " + nroSocio);
            } else {
                System.out.println(separador);
                System.out.printf("SOCIO: %s (%s)%n", nombreSocio, runSocio);
                System.out.printf("RESUMEN: %d créditos | Monto total: $%.2f%n", totalCreditos, totalMonto);
                System.out.println("Detalle obtenido desde TU FUNCIÓN get_detalle_creditos_socio");
            }

            rs.close();
            cs.close();

        } catch (SQLException e) {
            System.err.println("Error consultando detalle: " + e.getMessage());
        }
    }

    /**
     * 4. EJECUTAR tu proceso de pagos y mostrar resultado
     */
    public static void ejecutarProcesoPagos() {
        System.out.println("\n=== EJECUTANDO PROCESO DE PAGOS (PKG_CREDITOS) ===");

        try (Connection conn = ConexionOracle.getConnection()) {

            System.out.println("Iniciando proceso de pagos...");

            CallableStatement cs = conn.prepareCall("{call PKG_CREDITOS.ejecutar_proceso_pagos}");
            cs.execute();
            cs.close();

            System.out.println("Proceso ejecutado. Verificando resultado...");

            // Verificar el estado del último proceso
            CallableStatement csStatus = conn.prepareCall("{? = call PKG_CREDITOS.get_status_proceso_pagos}");
            csStatus.registerOutParameter(1, Types.VARCHAR);
            csStatus.execute();

            String estado = csStatus.getString(1);
            System.out.println("📋 Estado del proceso: " + estado);

            if ("ÉXITO".equals(estado)) {
                System.out.println("¡Proceso completado exitosamente!");
                mostrarEstadisticasProceso(); // Mostrar estadísticas actualizadas
            } else {
                System.out.println("El proceso tuvo problemas. Revisa los errores en ERROR_PAGO_MENSUAL_CREDITO");
            }

            csStatus.close();

        } catch (SQLException e) {
            System.err.println("Error ejecutando proceso: " + e.getMessage());
        }
    }




}