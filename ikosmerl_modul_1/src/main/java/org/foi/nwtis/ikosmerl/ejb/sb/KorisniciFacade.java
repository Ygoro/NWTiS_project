package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;

/**
 * Fasada za korisnike.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class KorisniciFacade extends AbstractFacade<Korisnici> implements KorisniciFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KorisniciFacade() {
        super(Korisnici.class);
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a izvršava upit kojim se provjerava da li u tablici Korisnici postoji korisnik
     * unesenog korisničkog imena i lozinke.
     * 
     * @param korime korisničko ime korisnika kojeg se autentificira
     * @param lozinka lozinka korisnika kojeg se autentificira
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Korisnici> autentificirajKorisnika(String korime, String lozinka) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
        Root<Korisnici> korisnici = cq.from(Korisnici.class);
        cq.where(
                cb.and(
                        cb.equal(korisnici.get("korIme"), korime),
                        cb.equal(korisnici.get("lozinka"), lozinka)
                )
        );
        
        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
