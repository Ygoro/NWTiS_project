package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairportslog;

/**
 * Fasada za logove vezane uz podatke preuzetih aerodroma.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class MyairportslogFacade extends AbstractFacade<Myairportslog> implements MyairportslogFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportslogFacade() {
        super(Myairportslog.class);
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a izvršava upit kojim se provjerava da li u tablici myairportslog 
     * postoji aerodrom s unesenim ident-om i datumom preuzimanja.
     *
     * @param ident identifikacijska oznaka aerodroma
     * @param flightdate datum preuzimanja
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Myairportslog> provjeriPostojiAerodrom(String ident, Date flightdate) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Myairportslog> cq = cb.createQuery(Myairportslog.class);
        Root<Myairportslog> aerodromiLog = cq.from(Myairportslog.class);
        cq.where(
                cb.and(
                        cb.equal(aerodromiLog.get("myairportslogPK").get("ident"), ident),
                        cb.equal(aerodromiLog.get("myairportslogPK").get("flightdate"), flightdate)
                )
        );

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    @Override
    public List<Myairportslog> dajZapiseTrazenogAerodroma(String ident) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Myairportslog> cq = cb.createQuery(Myairportslog.class);
        Root<Myairportslog> aerodromiLog = cq.from(Myairportslog.class);
        cq.where(cb.equal(aerodromiLog.get("myairportslogPK").get("ident"), ident));

        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
