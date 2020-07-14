package org.foi.nwtis.ikosmerl.ws.klijenti;

import java.util.List;

/**
 * Klasa koja predstavlja klijenta JAX-WS servisa.
 * Klasa sadrži metode koje predstavljaju pristupne metode metoda SOAP web servisa aplikacije 2.
 * 
 * @author Igor Košmerl
 */
public class Aplikacija2WS {
    
    /**
     * Pristupna metoda metode dajAerodromeKorisnika web servisa aplikacije 2 projekta.
     * 
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @return lista svih aerodroma korisnika
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korisnik, String lozinka) {
        List<Aerodrom> aerodromi = null;
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            aerodromi = port.dajAerodromeKorisnika(korisnik, lozinka);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return aerodromi;
    }
    
    /**
     * Pristupna metoda metode dajAerodromeDrzava web servisa aplikacije 2 projekta.
     * 
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param drzava država za koju se pretražuju aerodromi
     * @return lista svih aerodroma tražene države
     */
    public List<Aerodrom> dajAerodromeDrzava(String korisnik, String lozinka, String drzava) {
        List<Aerodrom> aerodromi = null;
        
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            aerodromi = port.dajAerodromeDrzava(korisnik, lozinka, drzava);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return aerodromi;
    }
    
    /**
     * Pristupna metoda metode dajAerodromeNaziv web servisa aplikacije 2 projekta.
     * 
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param naziv naziv za koji se pretražuju aerodromi
     * @return lista svih aerodroma traženog naziva
     */
    public List<Aerodrom> dajAerodromeNaziv(String korisnik, String lozinka, String naziv) {
        List<Aerodrom> aerodromi = null;
        
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            aerodromi = port.dajAerodromeNaziv(korisnik, lozinka, naziv);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return aerodromi;
    }
    
}
