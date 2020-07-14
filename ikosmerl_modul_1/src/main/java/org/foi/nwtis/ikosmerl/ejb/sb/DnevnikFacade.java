package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;

/**
 * Fasada za dnevnik.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> implements DnevnikFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }
    
    @Override
    public List<Dnevnik> dajPodatkeDnevnikaKorisnika(String korime) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Dnevnik> cq = cb.createQuery(Dnevnik.class);
        Root<Dnevnik> podaciDnevnika = cq.from(Dnevnik.class);
        cq.where(cb.equal(podaciDnevnika.get("korisnik"), korime));

        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
