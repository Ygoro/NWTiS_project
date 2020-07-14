package org.foi.nwtis.ikosmerl.ws.klijenti;

import java.util.List;

/**
 * Klijent SOAP web servisa AerodromiWS.
 *
 * @author Igor Košmerl
 */
public class Aplikacija2WS {

    /**
     * Pristupna metoda metode dajAerodromeKorisnika web servisa AerodromiWS.
     *
     * @param korime korisničko ime za autentifikaciju
     * @param lozinka lozinka za autentifikaciju
     * @return lista aerodroma korisnika ili null ako dođe do greške kod pristupanja metodi servisa
     */
    public List<Aerodrom> dajAerodromeKorisnika(String korime, String lozinka) {
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            List<Aerodrom> result = port.dajAerodromeKorisnika(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * Pristupna metoda metode dajLetoveAviona web servisa AerodromiWS.
     *
     * @param korime korisničko ime za autentifikaciju
     * @param lozinka lozinka za autentifikaciju
     * @param icao24 oznaka aerodroma za kojeg se pretražuju letovi
     * @param vrijemeOd vrijeme od kojeg se traže letovi aviona
     * @param vrijemeDo vrijeme do kojeg se traže letovi aviona
     * @return lista objekata tipa AvionLeti koji predstavljaju letove aviona odabranog aerodroma u vremenskom intervalu
     */
    public List<AvionLeti> dajLetoveAviona(String korime, String lozinka, String icao24, long vrijemeOd, long vrijemeDo) {
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            List<AvionLeti> result = port.dajLetoveAviona(korime, lozinka, icao24, vrijemeOd, vrijemeDo);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * Pristupna metoda metode dajPoletjeleAvioneAerodroma web servisa AerodromiWS.
     *
     * @param korime korisničko ime za autentifikaciju
     * @param lozinka lozinka za autentifikaciju
     * @param icao oznaka aerodroma za kojeg se traže poletjeli avioni
     * @param vrijemeOd vrijeme od kojeg se traže poletjeli avioni
     * @param vrijemeDo vrijeme do kojeg se traže poletjeli avioni
     * @return lista aviona poletjelih s odabranog aerodroma
     */
    public List<AvionLeti> dajPoletjeleAvioneAerodroma(String korime, String lozinka, String icao, long vrijemeOd, long vrijemeDo) {
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            List<AvionLeti> result = port.dajPoletjeleAvioneAerodroma(korime, lozinka, icao, vrijemeOd, vrijemeDo);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * Pristupna metoda metode dajAerodromeKorisnikaUnutarGranica web servisa AerodromiWS.
     *
     * @param korime korisničko ime za autentifikaciju
     * @param lozinka lozinka za autentifikaciju
     * @param icao oznaka aerodroma od kojeg se traže aerodromi unutar granica udaljenosti
     * @param donjaGranica donja granica udaljenosti (min)
     * @param gornjaGranica gornja granica udaljenosti (max)
     * @return lista aerodroma udaljenih od odabranog aerodroma unutar unesenih granica udaljenosti
     */
    public List<Aerodrom> dajAerodromeKorisnikaUnutarGranica(String korime, String lozinka, String icao,
            int donjaGranica, int gornjaGranica) {
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            List<Aerodrom> result = port.dajAerodromeKorisnikaUnutarGranica(korime, lozinka, icao, donjaGranica, gornjaGranica);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Pristupna metoda metode dajUdaljenostAerodroma web servisa AerodromiWS.
     * 
     * @param korime korisničko ime za autentifikaciju
     * @param lozinka lozinka za autentifikaciju
     * @param prviIcao početni aerodrom za mjerenje udaljenosti
     * @param drugiIcao posljednji aerodrom za mjerenje udaljenosti
     * @return udaljenost između unesenih aerodroma u kilometrima
     */
    public int dajUdaljenostAerodroma(String korime, String lozinka, String prviIcao, String drugiIcao) {
        try {
            Aplikacija2_Service service = new Aplikacija2_Service();
            Aplikacija2 port = service.getAplikacija2Port();
            int result = port.dajUdaljenostAerodroma(korime, lozinka, prviIcao, drugiIcao);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
}
