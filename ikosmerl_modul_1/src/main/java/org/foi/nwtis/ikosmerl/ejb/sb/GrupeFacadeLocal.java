package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Grupe;

/**
 * Sučelje koje implementira fasada GrupeFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface GrupeFacadeLocal {

    void create(Grupe grupe);

    void edit(Grupe grupe);

    void remove(Grupe grupe);

    Grupe find(Object id);

    List<Grupe> findAll();

    List<Grupe> findRange(int[] range);

    int count();
    
}
