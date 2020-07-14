package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 1 četvrte aplikacije.
 * Pogled 1 služi za prijavu korisnika, no na isti pogled dodan je i odabir lokalizacije.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled1")
@SessionScoped
public class ZrnoPogled1 implements Serializable {

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String lozinka;
    @Getter
    @Setter
    private String uneseniKorisnik;
    @Getter
    @Setter
    private String unesenaLozinka;
    @Getter
    @Setter
    private boolean prijavljen = false;

    @Inject
    ServletContext context;
    
    @Inject
    Lokalizacija lokalizacija;

    public ZrnoPogled1() {
    }

    /**
     * Prijavljuje korisnika i provjerava metodu za autentifikaciju fasade Korisnici.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String prijaviKorisnika() {
        if (!korisniciFacade.autentificirajKorisnika(uneseniKorisnik, unesenaLozinka).isEmpty()) {
            korisnik = uneseniKorisnik;
            lozinka = unesenaLozinka;
            prijavljen = true;
        } else {
            prijavljen = false;
        }
        return "";
    }

    /**
     * Odjavljuje korisnika postavljanjem svih ključnih varijabli na praznu vrijednost.
     *
     * @return putanju do pogleda za prijavu korisnika
     */
    public String odjaviKorisnika() {
        korisnik = "";
        lozinka = "";
        uneseniKorisnik = "";
        unesenaLozinka = "";
        prijavljen = false;
        return "/pogled1.xhtml";
    }

}
