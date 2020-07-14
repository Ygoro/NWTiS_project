package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairports;

/**
 * Fasada za aerodrome.
 *
 * @author Igor Košmerl
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> implements AirportsFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }

    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke vezane uz aerodrome unesenog naziva. Naziv može biti djelomično unesen
     * jer se koristi funkcija like().
     *
     * @param naziv naziv za koji se pretražuju aerodromi
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airports> vratiAerodromeNaziva(String naziv) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        cq.where(cb.like(aerodromi.get("name"), naziv));

        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke vezane uz aerodrome unesene države.
     *
     * @param drzava država za koju se pretražuju aerodromi
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airports> vratiAerodromeDrzave(String drzava) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        cq.where(cb.equal(aerodromi.get("isoCountry"), drzava));

        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke vezane uz aerodrome odabranog korisnika.
     *
     * @param korisnik objekt tipa Korisnici za kojeg se pretražuju aerodromi
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airports> vratiAerodromeKorisnika(Korisnici korisnik) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        Join<Airports, Myairports> myAirports = aerodromi.join("myairportsList");
        cq.where(cb.equal(myAirports.get("username"), korisnik));

        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke vezane uz aerodrome svih korisnika.
     *
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airports> vratiAerodromeSvihKorisnika() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        Join<Airports, Myairports> myAirports = aerodromi.join("myairportsList");
        cq.select(aerodromi);

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke vezane uz traženi aerodrom traženog korisnika.
     * 
     * @param korisnik objekt tipa Korisnici za kojeg se pretražuju aerodromi
     * @param icao oznaka aerodroma za kojeg se prikazuju podaci
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airports> vratiAerodromeOznaka(Korisnici korisnik, String icao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = cq.from(Airports.class);
        Join<Airports, Myairports> myAirports = aerodromi.join("myairportsList");
        cq.where(
                cb.and(
                        cb.equal(aerodromi.get("ident"), icao),
                        cb.equal(myAirports.get("username"), korisnik)
                )
        );

        return getEntityManager().createQuery(cq).getResultList();
    }

}
