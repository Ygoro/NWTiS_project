package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 1 treće aplikacije. Pogled 1 služi za registraciju, prijavu i
 * odjavu korisnika te prikaz popisa svih korisnika.
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
    private String korisnikRegistracija;
    @Getter
    @Setter
    private String lozinka;
    @Getter
    @Setter
    private String lozinkaRegistracija;
    @Getter
    @Setter
    private String uneseniKorisnik;
    @Getter
    @Setter
    private String unesenaLozinka;
    @Getter
    @Setter
    private String imeRegistracija;
    @Getter
    @Setter
    private String prezimeRegistracija;
    @Getter
    @Setter
    private String emailAdresaRegistracija;
    @Getter
    private boolean prijavljen = false;
    @Getter
    @Setter
    private List<Korisnici> popisKorisnika = new ArrayList<>();

    @Inject
    ZrnoPogled4 pogled4;

    @Inject
    ServletContext context;

    public ZrnoPogled1() {
    }

    /**
     * Kreira novog korisnika (registrira korisnika) u obje baze podataka.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String registrirajKorisnika() {
        Korisnici noviKorisnik = new Korisnici();
        postaviPodatkeKorisnika(noviKorisnik);
        if (korisniciFacade.autentificirajKorisnika(uneseniKorisnik, unesenaLozinka).isEmpty()) {
            korisniciFacade.create(noviKorisnik);
            BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
            int port = Integer.parseInt(konf.getKonfig().dajPostavku("socketserver.port"));
            String server = konf.getKonfig().dajPostavku("socketserver.server");
            try (
                    Socket veza = new Socket(server, port);
                    OutputStreamWriter pisacIzlaznogToka = new OutputStreamWriter(veza.getOutputStream(), "UTF-8");
                    InputStream is = veza.getInputStream();) {
                pisacIzlaznogToka.write("KORISNIK " + noviKorisnik.getKorIme() + "; LOZINKA " + noviKorisnik.getLozinka() + "; DODAJ;");
                pisacIzlaznogToka.flush();
                veza.shutdownOutput();

                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                br.readLine();
                veza.shutdownInput();
            } catch (IOException ex) {
                System.out.println("[Greška] Neuspješno spajanje na poslužitelja aplikacije 1!");
            }
            prikaziSveKorisnike();
            resetirajPoljaNakonRegistracije(noviKorisnik);
        }
        return "";
    }

    /**
     * Pomoćna metoda za postavljanje podataka objekta korisnika koji će se unjeti u bazu podataka.
     *
     * @param noviKorisnik korisnik čiji se podaci postavljaju
     */
    private void postaviPodatkeKorisnika(Korisnici noviKorisnik) {
        noviKorisnik.setKorIme(korisnikRegistracija);
        noviKorisnik.setIme(imeRegistracija);
        noviKorisnik.setPrezime(prezimeRegistracija);
        noviKorisnik.setLozinka(lozinkaRegistracija);
        noviKorisnik.setEmailAdresa(emailAdresaRegistracija);
        noviKorisnik.setDatumKreiranja(new Date());
        noviKorisnik.setDatumPromjene(new Date());
    }

    /**
     * Pomoćna metoda za resetiranje unesenih vrijednosti za registraciju korisnika nakon registracije.
     *
     * @param noviKorisnik korisnik čiji se podaci resetiraju
     */
    private void resetirajPoljaNakonRegistracije(Korisnici noviKorisnik) {
        korisnikRegistracija = "";
        imeRegistracija = "";
        prezimeRegistracija = "";
        lozinkaRegistracija = "";
        emailAdresaRegistracija = "";
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
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String odjaviKorisnika() {
        korisnik = "";
        lozinka = "";
        uneseniKorisnik = "";
        unesenaLozinka = "";
        prijavljen = false;
        return "";
    }

    /**
     * Kreira popis svih korisnika.
     */
    @PostConstruct
    public void prikaziSveKorisnike() {
        popisKorisnika = korisniciFacade.findAll();
    }

}
