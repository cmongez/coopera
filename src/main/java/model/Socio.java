package model;

import java.sql.Date;

public class Socio {
    private int nroSocio;
    private int numRun;
    private String dvRun;
    private String pNombre;
    private String sNombre;
    private String apPaterno;
    private String apMaterno;
    private Date fechaNacimiento;
    private String correo;
    private int fono;
    private String direccion;
    private int codRegion;
    private int codProvincia;
    private int codComuna;
    private int codProfOfic;
    private int codTipoSocio;

    // Getters y Setters
    public int getNroSocio() { return nroSocio; }
    public void setNroSocio(int nroSocio) { this.nroSocio = nroSocio; }

    public int getNumRun() { return numRun; }
    public void setNumRun(int numRun) { this.numRun = numRun; }

    public String getDvRun() { return dvRun; }
    public void setDvRun(String dvRun) { this.dvRun = dvRun; }

    public String getpNombre() { return pNombre; }
    public void setpNombre(String pNombre) { this.pNombre = pNombre; }

    public String getsNombre() { return sNombre; }
    public void setsNombre(String sNombre) { this.sNombre = sNombre; }

    public String getApPaterno() { return apPaterno; }
    public void setApPaterno(String apPaterno) { this.apPaterno = apPaterno; }

    public String getApMaterno() { return apMaterno; }
    public void setApMaterno(String apMaterno) { this.apMaterno = apMaterno; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getFono() { return fono; }
    public void setFono(int fono) { this.fono = fono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCodRegion() { return codRegion; }
    public void setCodRegion(int codRegion) { this.codRegion = codRegion; }

    public int getCodProvincia() { return codProvincia; }
    public void setCodProvincia(int codProvincia) { this.codProvincia = codProvincia; }

    public int getCodComuna() { return codComuna; }
    public void setCodComuna(int codComuna) { this.codComuna = codComuna; }

    public int getCodProfOfic() { return codProfOfic; }
    public void setCodProfOfic(int codProfOfic) { this.codProfOfic = codProfOfic; }

    public int getCodTipoSocio() { return codTipoSocio; }
    public void setCodTipoSocio(int codTipoSocio) { this.codTipoSocio = codTipoSocio; }
}
