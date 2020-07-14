package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.rest.klijenti.Aplikacija3RS;
import org.foi.nwtis.ikosmerl.ws.klijenti.Aplikacija2WS;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.OdgovorAerodrom;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 5 četvrte aplikacije. Pogled 5 služi prikazivanju podataka o
 * vlastitim aerodromima, brisanju praćenja aerodroma i brisanju letova aviona odaranog aerodroma korištenjem JAX_RS (REST) servisa
 * treće aplikacije. Osim toga, služi ispisivanju aerodroma unutar određene granice udaljenosti i sama udaljenost odabranih
 * aerodroma korištenjem web servisa iz druge aplikacije.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled5")
@ViewScoped
public class ZrnoPogled5 implements Serializable {

    @Inject
    ZrnoPogled1 pogled1;

    @Getter
    @Setter
    private Aplikacija2WS aplikacija2WS = new Aplikacija2WS();
    @Getter
    @Setter
    private Aplikacija3RS aplikacija3RS = new Aplikacija3RS();
    @Getter
    @Setter
    private Aerodrom[] aerodromiKorisnika;
    @Getter
    @Setter
    private String odabraniAerodrom;
    @Getter
    @Setter
    private String povratnaInformacija;
    @Getter
    @Setter
    private OdgovorAerodrom odgovor;
    @Getter
    @Setter
    private String unesenaGranicaMin;
    @Getter
    @Setter
    private String unesenaGranicaMaks;
    @Getter
    @Setter
    private List<org.foi.nwtis.ikosmerl.ws.klijenti.Aerodrom> aerodromiUnutarGranica = new ArrayList<>();
    @Getter
    @Setter
    private String[] odabraniAerodromiUdaljenost;
    @Getter
    @Setter
    private String udaljenostAerodromaString;

    public ZrnoPogled5() {
    }
    
    /**
     * Puni listu aerodroma prijavljenog korisnika za prikaz u padajućem izborniku. 
     */
    @PostConstruct
    public void prikaziVlastiteAerodrome() {
        odgovor = aplikacija3RS.dajAerodomeKorisnika(pogled1.getKorisnik(),pogled1.getLozinka(),OdgovorAerodrom.class);
        aerodromiKorisnika = odgovor.getOdgovor();
    }
    
    /**
     * Briše korisnikovo praćenje aerodroma korištenjem metode JAX-RS servisa treće aplikacije.
     * 
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String obrisiPracenjeAerodroma() {
        Response response = aplikacija3RS.obrisiPracenjeAerodromaKorisnika(pogled1.getKorisnik(), pogled1.getLozinka(), 
                odabraniAerodrom);
        if(response.getStatus() == 400) {
            povratnaInformacija = "Odabrani aerodrom posjeduje letove pa ga je nemoguće obrisati!";
        } else {
            povratnaInformacija = "Uspješno obrisan aerodrom " + odabraniAerodrom;
            prikaziVlastiteAerodrome();
        }
        return "";
    }
    
    /**
     * Briše letove aviona odabranog aerodroma korištenjem metode JAX-RS servisa treće aplikacije.
     * 
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String obrisiLetoveAvionaAerodroma() {
        Response response = aplikacija3RS.obrisiLetoveAvionaAerodromaKorisnika(pogled1.getKorisnik(), pogled1.getLozinka(), 
                odabraniAerodrom);
        aerodromiKorisnika = odgovor.getOdgovor();
        if(response.getStatus() == 200) {
            povratnaInformacija = "Uspješno obrisani letovi aerodroma " + odabraniAerodrom;
        }
        return "";
    }
    
    /**
     * Puni listu aerodroma unutar granica pozivom metode JAX-WS web servisa druge aplikacije.
     * 
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajAerodromeUnutarGranica() {
        aerodromiUnutarGranica = aplikacija2WS.dajAerodromeKorisnikaUnutarGranica(pogled1.getKorisnik(), pogled1.getLozinka(), 
                odabraniAerodrom, Integer.parseInt(unesenaGranicaMin), Integer.parseInt(unesenaGranicaMaks));
        return "";
    }
    
    /**
     * Vraća udaljenost između dva odabrana aerodroma pozivom metode JAX-WS web servisa druge aplikacije.
     * 
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajUdaljenostIzmeduAerodroma() {
        String icaoPrvogAerodroma = odabraniAerodromiUdaljenost[0];
        String icaoDrugogAerodroma = odabraniAerodromiUdaljenost[1];
        int udaljenost = aplikacija2WS.dajUdaljenostAerodroma(pogled1.getKorisnik(), pogled1.getLozinka(), 
                icaoPrvogAerodroma, icaoDrugogAerodroma);
        udaljenostAerodromaString = String.valueOf(udaljenost).concat(" km");
        return "";
    }

}
