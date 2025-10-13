import java.util.Scanner;
import logic.ProcesarPagos;
import logic.Reportes;

public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== COOPERA - Sistema de Gestión Financiera ===");
            System.out.println("1. Ejecutar proceso de pagos");
            System.out.println("2. Mostrar reporte de créditos por socio");
            System.out.println("3. Consultar detalle de créditos por socio");
            System.out.println("4. Ver estadísticas del proceso");
            System.out.println("5. Reporte completo (créditos + estadísticas)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> ProcesarPagos.ejecutarProcesoPagos();
                case 2 -> Reportes.mostrarReporteCreditosSocios();
                case 3 -> {
                    System.out.print("Ingrese número de socio: ");
                    int nroSocio = sc.nextInt();
                    Reportes.mostrarDetalleSocio(nroSocio);
                }
                case 4 -> Reportes.mostrarEstadisticasProceso();
                case 5 -> {
                    System.out.println("=== REPORTE COMPLETO DEL SISTEMA ===");
                    Reportes.mostrarReporteCreditosSocios();
                    Reportes.mostrarEstadisticasProceso();
                }
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
