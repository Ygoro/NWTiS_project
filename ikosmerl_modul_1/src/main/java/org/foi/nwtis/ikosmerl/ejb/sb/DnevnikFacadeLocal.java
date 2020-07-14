package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;

/**
 * Sučelje koje implementira fasada DnevnikFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface DnevnikFacadeLocal {

    void create(Dnevnik dnevnik);

    void edit(Dnevnik dnevnik);

    void remove(Dnevnik dnevnik);

    Dnevnik find(Object id);

    List<Dnevnik> findAll();

    List<Dnevnik> findRange(int[] range);

    int count();
    
    List<Dnevnik> dajPodatkeDnevnikaKorisnika(String korime);
    
}
