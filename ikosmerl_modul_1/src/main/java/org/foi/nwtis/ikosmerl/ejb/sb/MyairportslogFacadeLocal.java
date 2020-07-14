package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairportslog;

/**
 * Sučelje koje implementira fasada MyairportslogFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface MyairportslogFacadeLocal {

    void create(Myairportslog myairportslog);

    void edit(Myairportslog myairportslog);

    void remove(Myairportslog myairportslog);

    Myairportslog find(Object id);

    List<Myairportslog> findAll();

    List<Myairportslog> findRange(int[] range);

    int count();
    
    List<Myairportslog> provjeriPostojiAerodrom(String ident, Date flightdate);
    
    List<Myairportslog> dajZapiseTrazenogAerodroma(String ident);
}
