package org.foi.nwtis.ikosmerl.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa za manipulaciju s podacima korisnika.
 * 
 * @author Igor Košmerl
 */
public class KorisnikDAO {
    
    /**
     * Izvršava upit kojim se provjerava prisutnost para korisničkog imena i lozinke u tablici korisnici.
     * @param korime - korisničko ime korisnika
     * @param lozinka - lozinka korisnika
     * @param bpk - konfiguracija baze podataka
     * @return true ako korisnik s unesenim parom korsiničkog imena i lozinke postoji; inače false
     */
    public Boolean autentificirajKorisnika(String korime, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "SELECT * FROM korisnici WHERE KORISNICKO_IME = '" + korime + "' AND LOZINKA = '" + lozinka + "'";
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Izvršava upit za dodavanje novog korisnika tablicu korisnici.
     * 
     * @param korime - korisničko ime korisnika
     * @param lozinka - lozinka korisnika
     * @param bpk - konfiguracija baze podataka
     * @return true ako je korisnik uspješno dodan; inače false
     */
    public boolean dodajKorisnika(String korime, String lozinka, BP_Konfiguracija bpk) {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        String upit = "INSERT INTO korisnici (KORISNICKO_IME, LOZINKA) VALUES ('" + korime + "', '" + lozinka + "')";
        try {
            Class.forName(bpk.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement()) {
                int brojAzuriranja = s.executeUpdate(upit);
                return brojAzuriranja == 1;
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}