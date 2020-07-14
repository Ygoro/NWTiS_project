package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik_;
import org.foi.nwtis.ikosmerl.ws.klijenti.Aerodrom;
import org.foi.nwtis.ikosmerl.ws.klijenti.Aplikacija2WS;
import org.foi.nwtis.ikosmerl.ws.klijenti.AvionLeti;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 4 četvrte aplikacije. Pogled 4 služi prikazivanju podataka o
 * aerodromima korisnika, letovima aviona odabranog aerodroma u zadanom intervalu i letova za odabrani avion u zadanom intervalu.
 * Svi podaci prikazuju se ozivom JAX-WS (SOAP) web servisa druge aplikacije.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled4")
@ViewScoped
public class ZrnoPogled4 implements Serializable {

    @Inject
    ZrnoPogled1 pogled1;
    
    @Getter
    @Setter
    private Aplikacija2WS aplikacija2WS = new Aplikacija2WS();
    @Getter
    @Setter
    private List<Aerodrom> aerodromiKorisnika = new ArrayList<>();
    @Getter
    @Setter
    private List<AvionLeti> letoviAviona = new ArrayList<>();
    @Getter
    @Setter
    private List<AvionLeti> poletjeliAvioni = new ArrayList<>();
    @Getter
    @Setter
    private String unesenoVrijemeOd;
    @Getter
    @Setter
    private String unesenoVrijemeDo;

    public ZrnoPogled4() {
    }
    
    /**
     * Vraća listu aerodroma korisnika pozivom metode JAX-WS web servisa.
     * 
     * @return lista aerodroma korisnika
     */
    public List<Aerodrom> dajAerodromeKorisnika() {
        return aplikacija2WS.dajAerodromeKorisnika(pogled1.getKorisnik(), pogled1.getLozinka());
    }
    
    /**
     * Puni listu poletjelih aviona u odabranom vremenskom intervalu pozivom metode JAX-WS web servisa.
     * 
     * @param identOdabranogAerodroma oznaka odabranog aerodroma korisnika
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajPoletjeleAvioneAerodroma(String identOdabranogAerodroma) {
        try {
            long vrijemeOdEpoch = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(unesenoVrijemeOd).getTime()/1000;
            long vrijemeDoEpoch = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(unesenoVrijemeDo).getTime()/1000;
            poletjeliAvioni = aplikacija2WS.dajPoletjeleAvioneAerodroma(pogled1.getKorisnik(), pogled1.getLozinka(), 
                    identOdabranogAerodroma, vrijemeOdEpoch, vrijemeDoEpoch);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
    
    /**
     * Puni listu letova aviona u odabranom vremenskom intervalu pozivom metode JAX-WS web servisa.
     * 
     * @param icao24OdabranogAviona oznaka odabranog poletjelog aviona
     * @return prazan string kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajLetoveAviona(String icao24OdabranogAviona) {
        try {
            long vrijemeOdEpoch = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(unesenoVrijemeOd).getTime()/1000;
            long vrijemeDoEpoch = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(unesenoVrijemeDo).getTime()/1000;
            letoviAviona = aplikacija2WS.dajLetoveAviona(pogled1.getKorisnik(), pogled1.getLozinka(), 
                    icao24OdabranogAviona, vrijemeOdEpoch, vrijemeDoEpoch);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
}
