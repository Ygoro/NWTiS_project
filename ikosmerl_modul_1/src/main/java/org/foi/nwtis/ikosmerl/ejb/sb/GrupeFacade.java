package org.foi.nwtis.ikosmerl.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.ikosmerl.ejb.eb.Grupe;

/**
 * Fasada za grupe korisnika.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class GrupeFacade extends AbstractFacade<Grupe> implements GrupeFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupeFacade() {
        super(Grupe.class);
    }
    
}
