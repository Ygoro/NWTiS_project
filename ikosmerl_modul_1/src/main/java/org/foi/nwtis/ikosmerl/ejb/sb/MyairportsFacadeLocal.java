package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairports;

/**
 * Sučelje koje implementira fasada MyairportsFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface MyairportsFacadeLocal {

    void create(Myairports myairports);

    void edit(Myairports myairports);

    void remove(Myairports myairports);

    Myairports find(Object id);

    List<Myairports> findAll();

    List<Myairports> findRange(int[] range);

    int count();
    
    List<String> dajAerodromeZaKojeSePreuzimajuPodaci();
    
    List<Myairports> dajAerodromKorisnikaOznake(String korime, String icao);
}
