import java.util.Scanner;
import logic.ProcesarPagos;
import logic.Reportes;
import java.sql.Date;
import dao.SocioDAO;
import model.Socio;
import dao.AuditoriaDAO;

public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SocioDAO socioDAO = new SocioDAO();
        AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

        int opcion;

        do {
            System.out.println("\n=== COOPERA - Sistema de Gestión Financiera ===");
            System.out.println("1. Ejecutar proceso de pagos");
            System.out.println("2. Mostrar reporte de créditos por socio");
            System.out.println("3. Consultar detalle de créditos por socio");
            System.out.println("4. Ver estadísticas del proceso");
            System.out.println("5. Reporte completo (créditos + estadísticas)");
            System.out.println("6. Registrar nuevo socio");
            System.out.println("7. Listar socios registrados");
            System.out.println("8. Ver auditoría de procesos");
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
                case 6 -> { // Registrar nuevo socio
                    Socio socio = new Socio();

                    System.out.print("RUN (sin DV): ");
                    socio.setNumRun(sc.nextInt());
                    System.out.print("DV: ");
                    socio.setDvRun(sc.next());
                    System.out.print("Primer nombre: ");
                    socio.setpNombre(sc.next());
                    System.out.print("Segundo nombre: ");
                    socio.setsNombre(sc.next());
                    System.out.print("Apellido paterno: ");
                    socio.setApPaterno(sc.next());
                    System.out.print("Apellido materno: ");
                    socio.setApMaterno(sc.next());
                    System.out.print("Fecha nacimiento (YYYY-MM-DD): ");
                    socio.setFechaNacimiento(Date.valueOf(sc.next()));
                    System.out.print("Correo electrónico: ");
                    socio.setCorreo(sc.next());
                    System.out.print("Teléfono: ");
                    socio.setFono(sc.nextInt());
                    sc.nextLine(); // limpia buffer
                    System.out.print("Dirección: ");
                    socio.setDireccion(sc.nextLine());

                    // Por ahora dejamos fijos estos valores (puedes mejorarlos después con selects)
                    socio.setCodRegion(1);
                    socio.setCodProvincia(1);
                    socio.setCodComuna(1);
                    socio.setCodProfOfic(1);
                    socio.setCodTipoSocio(1);

                    String resultado = socioDAO.registrarSocio(socio);
                    System.out.println(resultado);
                }
                case 7 -> socioDAO.listarSocios();
                case 8 -> auditoriaDAO.listarAuditoria();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
