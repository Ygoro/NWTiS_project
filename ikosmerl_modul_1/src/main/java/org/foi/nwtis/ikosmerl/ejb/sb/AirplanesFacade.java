package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;

/**
 *
 * @author Igor Košmerl
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> implements AirplanesFacadeLocal {

    @PersistenceContext(unitName = "NWTiS_projekt_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirplanesFacade() {
        super(Airplanes.class);
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke o poletjelim avionima traženog aerodroma i vremenskog intervala.
     * 
     * @param ident oznaka aerodroma za koji se traže poletjeli avioni
     * @param vrijemeOd vrijeme od kojeg se traže avioni
     * @param vrijemeDo vrijeme do kojeg se traže avioni
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airplanes> vratiPoletjeleAvioneAerodromaUIntervalu(String ident, long vrijemeOd, long vrijemeDo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airplanes> cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> avioni = cq.from(Airplanes.class);
        cq.where(
                cb.and(
                        cb.equal(avioni.get("estdepartureairport").get("ident"), ident),
                        cb.between(avioni.get("firstseen"), vrijemeOd, vrijemeDo)
                )
        );

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke o letovima traženog aviona i vremenskog intervala.
     * 
     * @param icao24 oznaka aviona za kojeg se traže letovi
     * @param vrijemeOd vrijeme od kojeg se traže avioni
     * @param vrijemeDo vrijeme do kojeg se traže avioni
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airplanes> dajLetoveAvionaUIntervalu(String icao24, long vrijemeOd, long vrijemeDo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airplanes> cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> avioni = cq.from(Airplanes.class);
        cq.where(
                cb.and(
                        cb.equal(avioni.get("icao24"), icao24),
                        cb.between(avioni.get("firstseen"), vrijemeOd, vrijemeDo)
                )
        );

        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /**
     * Metoda koja korištenjem CriteriaAPI-a vraća podatke o letovima aviona traženog aerodroma.
     * Metoda se koristi za provjeru prije brisanja aerodroma korisnika u RESTful servisu 3. aplikacije.
     * 
     * @param ident aerodrom za kojeg se traže letovi aviona
     * @return rezultat CriteriaAPI upita nad bazom podataka
     */
    @Override
    public List<Airplanes> dajPoletjeleAvioneAerodroma(String ident) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Airplanes> cq = cb.createQuery(Airplanes.class);
        Root<Airplanes> avioni = cq.from(Airplanes.class);
        cq.where(cb.equal(avioni.get("estdepartureairport").get("ident"), ident));

        return getEntityManager().createQuery(cq).getResultList();
    }
    
}
