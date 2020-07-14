package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;
import org.foi.nwtis.ikosmerl.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ikosmerl.websocket.Aplikacija2WebSocketKlijent;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 3 četvrte aplikacije.
 * Pogled 3 služi za prikaz aerodroma korisnika, njihovih geolokacijskih i meteo podataka i pretraživanje aerodroma.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled3")
@ViewScoped
public class ZrnoPogled3 implements Serializable {
    
    @Inject
    ZrnoPogled1 pogled1;
    
    @EJB
    MyairportsFacadeLocal myairportsFacade;
    
    @EJB
    AirportsFacadeLocal airportsFacade;
    
    @EJB
    KorisniciFacadeLocal korisniciFacade;
    
    @EJB
    MyairportslogFacadeLocal myairportslogFacade;
    
    @Getter
    @Setter
    private List<Airports> aerodromiKorisnika = new ArrayList<>();
    @Getter
    @Setter
    private List<Airports> sviAerodromi = new ArrayList<>();
    @Getter
    @Setter
    private List<Airports> filtriraniAerodromi = new ArrayList<>();
    @Getter
    @Setter
    private String nazivAerodromaSviAerodromi;
    @Getter
    @Setter
    private String nazivAerodromaGeoMeteo;
    @Getter
    @Setter
    private String gpsSirina;
    @Getter
    @Setter
    private String gpsDuzina;
    @Getter
    @Setter
    private String odabraniAerodrom;
    @Getter
    @Setter
    private String temperatura;
    @Getter
    @Setter
    private String vlaga;
    
    @Inject
    ServletContext context;
    
    public ZrnoPogled3() {
    }
    
    /**
     * Dohvaća sve aerodrome prijavljenog korisnika.
     */
    @PostConstruct
    public void dajAerodromeKorisnika() {
        Korisnici korisnik = korisniciFacade.find(pogled1.getKorisnik());
        aerodromiKorisnika = airportsFacade.vratiAerodromeKorisnika(korisnik);
    }
    
    /**
     * Dohvaća aerodrome svih korisnika.
     * 
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajAerodromeSvihKorisnika() {
        sviAerodromi = airportsFacade.vratiAerodromeNaziva(nazivAerodromaSviAerodromi);
        return "";
    }
    
    /**
     * Dodaje odabrani aerodrom korisniku slanjem zahtjeva na websocket druge aplikacije.
     * 
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode (ili poruku iznimke ako je uhvaćena)
     */
    public String dodajAerodromKorisniku() {
        try {
            Aplikacija2WebSocketKlijent klijent = new Aplikacija2WebSocketKlijent();
            klijent.spojiNaWebSocketServer("ws://localhost:8084/ikosmerl_aplikacija_2_web_modul/aerodromi");
            klijent.posaljiPorukuNaWebSocketServer("KORISNIK " + pogled1.getKorisnik() + "; ICAO " + odabraniAerodrom);
            klijent.odspojiKlijenta();
            dajAerodromeKorisnika();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
    
    /**
     * Preuzima podatke o geolokaciji i meteorološke podatke.
     *
     * @param aerodrom aerodrom odabran za prikaz geo i meteo podataka
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String preuzmiGeoMeteoPodatke(Airports aerodrom) {
        BP_Konfiguracija bpk = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        
        String liqToken =  bpk.getKonfig().dajPostavku("LocationIQ.token");
        LIQKlijent lIQKlijent = new LIQKlijent(liqToken);
        Lokacija lokacija = lIQKlijent.getGeoLocation(aerodrom.getName());
        gpsSirina = lokacija.getLatitude();
        gpsDuzina = lokacija.getLongitude();
        
        String owmApiKey = bpk.getKonfig().dajPostavku("OpenWeatherMap.apikey");
        dohvatiVlaguTemperaturu(owmApiKey);
        
        return "";
    }

    /**
     * Pomoćna metoda za dohvaćanje vlage i temperature s OpenWeatherMap klijenta.
     *
     * @param owmApiKey API ključ za OpenWeatherMap servis
     */
    public void dohvatiVlaguTemperaturu(String owmApiKey) {
        OWMKlijent owmKlijent = new OWMKlijent(owmApiKey);
        MeteoPodaci meteoPodaci = owmKlijent.getRealTimeWeather(gpsSirina, gpsDuzina);
        vlaga = meteoPodaci.getHumidityValue().toString();
        temperatura = meteoPodaci.getTemperatureValue().toString();
    }
    
    /**
     * Pomoćna metoda za pretvorbu objekta tipa Airports u Aerodrom.
     *
     * @param airport objekt tipa Airport koji predstavlja jedan aerodrom originalne liste (u svakoj iteraciji nadređene for petlje)
     * @return objekt tipa Aerodrom
     */
    private Aerodrom airportsToAerodrom(Airports airport) {
        String icaoAerodrom = airport.getIdent();
        String nazivAerodrom = airport.getName();
        String drzavaAerodrom = airport.getIsoCountry();
        Lokacija lokacijaAerodrom = new Lokacija();
        String koordinateAerodrom = airport.getCoordinates();
        lokacijaAerodrom.setLatitude(koordinateAerodrom.split(", ")[1]);
        lokacijaAerodrom.setLongitude(koordinateAerodrom.split(", ")[0]);
        return new Aerodrom(icaoAerodrom, nazivAerodrom, drzavaAerodrom, lokacijaAerodrom);
    }

}
