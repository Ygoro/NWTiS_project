package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;

/**
 * Sučelje koje implementira fasada KorisniciFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface KorisniciFacadeLocal {

    void create(Korisnici korisnici);

    void edit(Korisnici korisnici);

    void remove(Korisnici korisnici);

    Korisnici find(Object id);

    List<Korisnici> findAll();

    List<Korisnici> findRange(int[] range);

    int count();
    
    List<Korisnici> autentificirajKorisnika(String korime, String lozinka);
    
}
