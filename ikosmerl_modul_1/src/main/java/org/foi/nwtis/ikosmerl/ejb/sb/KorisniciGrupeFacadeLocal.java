package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.KorisniciGrupe;

/**
 * Sučelje koje implementira fasada KorisniciGrupeFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface KorisniciGrupeFacadeLocal {

    void create(KorisniciGrupe korisniciGrupe);

    void edit(KorisniciGrupe korisniciGrupe);

    void remove(KorisniciGrupe korisniciGrupe);

    KorisniciGrupe find(Object id);

    List<KorisniciGrupe> findAll();

    List<KorisniciGrupe> findRange(int[] range);

    int count();
    
}
