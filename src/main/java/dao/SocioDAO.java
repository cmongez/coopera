package dao;

import db.ConexionOracle;
import model.Socio;
import java.sql.*;

public class SocioDAO {

    public String registrarSocio(Socio socio) {
        String resultado = "";
        String sql = "{ ? = call registrar_socio(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.VARCHAR);

            cs.setInt(2, socio.getNumRun());
            cs.setString(3, socio.getDvRun());
            cs.setString(4, socio.getpNombre());
            cs.setString(5, socio.getsNombre());
            cs.setString(6, socio.getApPaterno());
            cs.setString(7, socio.getApMaterno());
            cs.setDate(8, socio.getFechaNacimiento());
            cs.setString(9, socio.getCorreo());
            cs.setInt(10, socio.getFono());
            cs.setString(11, socio.getDireccion());
            cs.setInt(12, socio.getCodRegion());
            cs.setInt(13, socio.getCodProvincia());
            cs.setInt(14, socio.getCodComuna());
            cs.setInt(15, socio.getCodProfOfic());
            cs.setInt(16, socio.getCodTipoSocio());

            cs.execute();
            resultado = cs.getString(1);

        } catch (SQLException e) {
            resultado = "Error al registrar socio: " + e.getMessage();
        }

        return resultado;
    }

    // Ejemplo de lectura
    public void listarSocios() {
        try (Connection conn = ConexionOracle.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nro_socio, pnombre, appaterno, correo FROM SOCIO ORDER BY nro_socio ASC");
             ResultSet rs = ps.executeQuery()) {

            System.out.printf("%-8s | %-15s | %-15s | %-25s%n", "NRO", "NOMBRE", "APELLIDO", "CORREO");
            System.out.println("-----------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-8d | %-15s | %-15s | %-25s%n",
                        rs.getInt("nro_socio"),
                        rs.getString("pnombre"),
                        rs.getString("appaterno"),
                        rs.getString("correo"));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar socios: " + e.getMessage());
        }
    }
}
