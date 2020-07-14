package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairports;

/**
 * Fasada za aerodrome korisnika.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class MyairportsFacade extends AbstractFacade<Myairports> implements MyairportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportsFacade() {
        super(Myairports.class);
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća listu jedinstvenih icao kodova aerodroma iz tablice MYAIRPORTS.
     * Metoda se koristi u WebSocketu aplikacije 2 za slanje poruke o ukupnom broju aerodroma 
     * za koje se preuzimaju podaci o letovima,
     * 
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<String> dajAerodromeZaKojeSePreuzimajuPodaci() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Myairports> aerodromi = cq.from(Myairports.class);
        cq.select(aerodromi.get("ident").get("ident")).distinct(true);

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća aerodrom
     * 
     * @param korime korisničko ime korisnika čiji se aerodrom pretražuje
     * @param ident oznaka aerodroma koji se pretražuje u bazi
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Myairports> dajAerodromKorisnikaOznake(String korime, String ident) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Myairports> cq = cb.createQuery(Myairports.class);
        Root<Myairports> aerodromiKorisnika = cq.from(Myairports.class);
        cq.where(
                cb.and(
                        cb.equal(aerodromiKorisnika.get("ident").get("ident"), ident),
                        cb.equal(aerodromiKorisnika.get("username").get("korIme"), korime)
                )
        );
        
        return getEntityManager().createQuery(cq).getResultList();
        
    }
    
}
