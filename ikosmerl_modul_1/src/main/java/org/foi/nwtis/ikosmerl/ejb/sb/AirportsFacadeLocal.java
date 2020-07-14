package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;

/**
 * Sučelje koje implementira fasada AirportsFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface AirportsFacadeLocal {

    void create(Airports airports);

    void edit(Airports airports);

    void remove(Airports airports);

    Airports find(Object id);

    List<Airports> findAll();

    List<Airports> findRange(int[] range);

    int count();
    
    List<Airports> vratiAerodromeNaziva(String naziv);
    
    List<Airports> vratiAerodromeDrzave(String drzava);
    
    List<Airports> vratiAerodromeKorisnika(Korisnici korisnik);
    
    List<Airports> vratiAerodromeSvihKorisnika();
    
    List<Airports> vratiAerodromeOznaka(Korisnici korisnik, String icao);
}
