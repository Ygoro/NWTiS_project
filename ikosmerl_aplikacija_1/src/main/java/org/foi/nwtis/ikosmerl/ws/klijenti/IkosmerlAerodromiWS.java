package org.foi.nwtis.ikosmerl.ws.klijenti;

/**
 * Klijent SOAP web servisa AerodromiWS.
 * 
 * @author Igor Košmerl
 */
public class IkosmerlAerodromiWS {
    
    /**
     * Pristupna metoda metode registrirajGrupu web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean registrirajGrupu(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.registrirajGrupu(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pristupna metoda metode deregistrirajGrupu web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean deregistrirajGrupu(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.deregistrirajGrupu(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pristupna metoda metode aktivirajGrupu web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean aktivirajGrupu(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            java.lang.Boolean result = port.aktivirajGrupu(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pristupna metoda metode blokirajGrupu web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean blokirajGrupu(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.blokirajGrupu(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pristupna metoda metode dajStatusGrupe web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return objekt tipa StatusGrupe, a null i tekst iznimke ako dođe do greške kod pozivanja metode
     */
    public StatusKorisnika dajStatusGrupe(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            StatusKorisnika result = port.dajStatusGrupe(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Pristupna metoda metode obrisiSveAerodromeGrupe web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean obrisiSveAerodromeGrupe(String korime, String lozinka) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.obrisiSveAerodromeGrupe(korime, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
    
    /**
     * Pristupna metoda metode dodajAerodromIcaoGrupi web servisa AerodromiWS.
     * 
     * @param korime - korisničko ime za autentikaciju
     * @param lozinka - lozinka za autentikaciju
     * @param icaoAerodroma - objekt klase Aerodrom koji se dodaje
     * @return true ako je metoda servisa uspješno pozvana (u suprotnom se ispisuje tekst iznimke i vraća vrijednost false)
     */
    public Boolean dodajAerodromIcaoGrupi(String korime, String lozinka, String icaoAerodroma) {
        
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            Boolean result = port.dodajAerodromIcaoGrupi(korime, lozinka, icaoAerodroma);
            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        return false;
    }
}
