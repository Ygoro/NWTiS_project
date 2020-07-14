package org.foi.nwtis.ikosmerl.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.ikosmerl.ejb.eb.KorisniciGrupe;

/**
 * Fasada za korisnike određene grupe korisnika.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class KorisniciGrupeFacade extends AbstractFacade<KorisniciGrupe> implements KorisniciGrupeFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KorisniciGrupeFacade() {
        super(KorisniciGrupe.class);
    }
    
}
