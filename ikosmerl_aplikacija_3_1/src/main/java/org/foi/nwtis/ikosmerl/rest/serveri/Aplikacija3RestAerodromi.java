package org.foi.nwtis.ikosmerl.rest.serveri;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairportslog;
import org.foi.nwtis.ikosmerl.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.ikosmerl.jms.JMSNWTiS_ikosmerl_2;
import org.foi.nwtis.ikosmerl.websocket.Aplikacija2WebSocketKlijent;
import org.foi.nwtis.ikosmerl.ws.klijenti.Aerodrom;
import org.foi.nwtis.ikosmerl.ws.klijenti.Aplikacija2WS;
import org.foi.nwtis.ikosmerl.ws.klijenti.Lokacija;
import org.foi.nwtis.podaci.Odgovor;

/**
 * JAX-RS RESTful web servis za rad s aerodromima. Zahtjev svake operacije upisuje se u dnevnik.
 *
 * @author Igor Košmerl
 */
@Path("aerodromi")
@RequestScoped
public class Aplikacija3RestAerodromi {

    @Context
    private UriInfo context;

    @EJB
    AirportsFacadeLocal airportsFacade;

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @EJB
    DnevnikFacadeLocal dnevnikFacade;

    @EJB
    MyairportsFacadeLocal myairportsFacade;

    @EJB
    AirplanesFacadeLocal airplanesFacade;

    @EJB
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB
    JMSNWTiS_ikosmerl_2 jmsNWTiSikosmerl2;

    private static int brojac = 0;

    /**
     * Creates a new instance of Aplikacija3RestAerodromi
     */
    public Aplikacija3RestAerodromi() {
    }

    /**
     * Vraća aerodrome koje prati korisnik.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @return odgovor na poslani zahtjev
     */
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {

        Aplikacija2WS aplikacija2WS = new Aplikacija2WS();
        List<Aerodrom> aerodromi = aplikacija2WS.dajAerodromeKorisnika(korisnik, lozinka);
        Odgovor odgovor = new Odgovor();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, aerodromi)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
        }

        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKomanda("Zatraženi aerodromi korisnika.");
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        dnevnikFacade.create(dnevnik);

        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Dodaje aerodrom korisniku koji je pozvao operaciju. Ne provodi se stvarno dodavanje nego se šalje poruka na krajnju točku
     * WebSocket-a aplikacije 2.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param icao icao oznaka aerodroma koji se dodaje korisniku
     * @return odgovor na poslani zahtjev
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajAerodromKorisniku(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @JsonProperty("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        List<Aerodrom> aerodromi = new ArrayList<>();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, aerodromi)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        Map jsonJavaRootObject = new Gson().fromJson(icao, Map.class);
        String icaoStringFromGson = (String) jsonJavaRootObject.get("icao");

        Aplikacija2WebSocketKlijent klijent = new Aplikacija2WebSocketKlijent();
        klijent.spojiNaWebSocketServer("ws://localhost:8084/ikosmerl_aplikacija_2_web_modul/aerodromi");
        try {
            klijent.posaljiPorukuNaWebSocketServer("KORISNIK " + korisnik + "; ICAO " + icaoStringFromGson);
            klijent.odspojiKlijenta();
        } catch (IOException ex) {
            Logger.getLogger(Aplikacija3RestAerodromi.class.getName()).log(Level.SEVERE, null, ex);
        }

        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKomanda("Aerodrom " + icaoStringFromGson + " dodan korisniku.");
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        dnevnikFacade.create(dnevnik);

        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Vraća podatke izabranog aerodroma ako je pridružen korisniku koji je pozvao operaciju.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param icao icao oznaka aerodroma čiji se podaci pretražuju
     * @return odgovor na poslani zahtjev
     */
    @Path("/{icao}")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodromKojiPratiKorisnik(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, aerodromi)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        getIcaoDohvacanjeILogika(korisnik, icao, aerodromi);

        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
        }
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Pomoćna metoda koja sadrži glavnu logiku treće operacije RESTful web servisa.
     *
     * @param korisnik korisnik za kojeg se kreira zapis u dnevniku i vraćaju podaci o aerodromu
     * @param icao oznaka aerodroma za koju se vraćaju podaci
     * @param aerodromi lista aerodroma koja se puni pronađenim aerodromom iz baze podataka
     */
    private void getIcaoDohvacanjeILogika(String korisnik, String icao, List<Aerodrom> aerodromi) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        List<Airports> airports = airportsFacade.vratiAerodromeOznaka(korisniciFacade.find(korisnik), icao);
        if (!airports.isEmpty()) {
            for (Airports airport : airports) {
                aerodromi.add(airportsToAerodrom(airport));
            }
            dnevnik.setKomanda("Vraćeni podaci aerodroma " + icao + " kojeg prati korisnik.");
        } else {
            dnevnik.setKomanda("Ne postoje podaci aerodroma " + icao + ".");
        }
        dnevnikFacade.create(dnevnik);
    }

    /**
     * Briše praćenje izabranog aerodroma za korisnika ako je pridružen korisniku koji je pozvao operaciju. Dodatno, prije brisanja
     * provjerava ima li odabrani aerodrom pridružene letove aviona, te ako ima ne briše podatke.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param icao icao oznaka aerodroma čiji se podaci o praćenju brišu
     * @return odgovor na poslani zahtjev
     */
    @Path("/{icao}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiPracenjeAerodromaKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        List<Aerodrom> aerodromi = new ArrayList<>();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, aerodromi)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        return deleteIcaoBrisanjeILogika(korisnik, icao, aerodromi, odgovor);
    }

    /**
     * Pomoćna metoda koja sadrži glavnu logiku 4. operacije RESTful web servisa.
     *
     * @param korisnik korisničko ime za koje se pretražuju aerodromi i radi zapis u dnevnik
     * @param icao oznaka aerodroma
     * @param aerodromi lista aerodroma koja se puni pronađenim aerodromom iz baze podataka
     * @return objet tipa Response (status 400 ako je neusješno brisanje, 200 ako je uspješno)
     */
    private Response deleteIcaoBrisanjeILogika(String korisnik, String icao, List<Aerodrom> aerodromi, Odgovor odgovor) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        if (myairportsFacade.dajAerodromKorisnikaOznake(korisnik, icao).isEmpty()
                || !airplanesFacade.dajPoletjeleAvioneAerodroma(icao).isEmpty()) {
            dnevnik.setKomanda("Neuspješno brisanje korisnikovog praćenja aerodroma " + icao + ". "
                    + "Aerodrom korisnika ne postoji ili sadrži letove.");
            dnevnikFacade.create(dnevnik);
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        } else {
            myairportsFacade.remove(myairportsFacade.dajAerodromKorisnikaOznake(korisnik, icao).get(0));
            dnevnik.setKomanda("Korisnikovo praćenje aerodroma " + icao + " uspješno obrisano.");
            dnevnikFacade.create(dnevnik);
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(aerodromi.toArray());
            return Response
                    .ok(odgovor)
                    .status(200)
                    .build();
        }

    }

    /**
     * Briše letove aviona izabranog aerodroma ako je pridružen korisniku koji je pozvao opearaciju.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param icao icao oznaka aerodroma čiji se podaci o letovima brišu
     * @return odgovor na poslani zahtjev
     */
    @Path("/{icao}/avioni")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiLetoveAvionaAerodromaKorisnika(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao) {

        Odgovor odgovor = new Odgovor();
        List<Airplanes> obrisaniAvioni = new ArrayList<>();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, obrisaniAvioni)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        return deleteIcaoAvioniBrisanjeILogika(korisnik, icao, obrisaniAvioni, odgovor);
    }

    /**
     * Pomoćna metoda koja sadrži glavnu logiku 5. operacije RESTful web servisa.
     *
     * @param korisnik korisničko ime za koje se pretražuju avioni aerodroma i radi zapis u dnevnik
     * @param icao oznaka aerodroma
     */
    private Response deleteIcaoAvioniBrisanjeILogika(String korisnik, String icao, List<Airplanes> obrisaniAvioni, Odgovor odgovor) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        if (myairportsFacade.dajAerodromKorisnikaOznake(korisnik, icao).isEmpty()
                || airplanesFacade.dajPoletjeleAvioneAerodroma(icao).isEmpty()) {
            dnevnik.setKomanda("Neuspješno brisanje letova aviona korisnikovog aerodroma " + icao + ". "
                    + "Aerodrom korisnika ne postoji ili ne sadrži letove.");
            dnevnikFacade.create(dnevnik);
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        } else {
            for (Airplanes avion : airplanesFacade.dajPoletjeleAvioneAerodroma(icao)) {
                airplanesFacade.remove(avion);
            }
            for (Myairportslog zapis : myairportslogFacade.dajZapiseTrazenogAerodroma(icao)) {
                myairportslogFacade.remove(zapis);
            }
            dnevnik.setKomanda("Letovi aviona korisnikovog aerodroma " + icao + " uspješno obrisani.");
            dnevnikFacade.create(dnevnik);
            odgovor.setStatus("10");
            odgovor.setPoruka("OK");
            odgovor.setOdgovor(obrisaniAvioni.toArray());
            return Response
                    .ok(odgovor)
                    .status(200)
                    .build();
        }
    }

    /**
     * Vraća aerodrome prema nazivu (naziv sličan stvarnom) ili državi. Ako nije unesen niti jedan parametar (niti naziv, niti
     * država), uzimaju se svi aerodromi naziva "%".
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param naziv naziv aerodroma izdvojen za korištenje u klasi resursa
     * @param drzava država aerodroma izdvojena za korištenje u klasi resursa
     * @return odgovor na poslani zahtjev
     */
    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodome(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) {

        Aplikacija2WS aplikacija2WS = new Aplikacija2WS();
        List<Aerodrom> aerodromi = new ArrayList<>();
        Odgovor odgovor = new Odgovor();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, aerodromi)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        aerodromi = getSviNazivDrzavaDohvacanjeIlogika(korisnik, naziv, aplikacija2WS, lozinka, drzava);

        if (aerodromi == null) {
            aerodromi = new ArrayList<>();
        }
        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(aerodromi.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Pomoćna metoda koja sadrži glavnu logiku 6. operacije RESTful web servisa.
     *
     * @param korisnik korisničko ime korisnika za kojeg se dohvaćaju aerodromi
     * @param naziv naziv aerodroma prema kojem se dohvaćaju podaci
     * @param aplikacija2WS objekt klase koja sadrži metode za dohvaćanje podataka korištenjem soap servisa druge aplikacije
     * @param lozinka lozinka korisnika za kojeg se dohvaćaju podaci
     * @param drzava država aerodroma prema kojoj se dohvaćaju podaci
     * @return
     */
    private List<Aerodrom> getSviNazivDrzavaDohvacanjeIlogika(String korisnik, String naziv,
            Aplikacija2WS aplikacija2WS, String lozinka, String drzava) {
        List<Aerodrom> aerodromi;
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        if (naziv != null && !naziv.isEmpty()) {
            aerodromi = aplikacija2WS.dajAerodromeNaziv(korisnik, lozinka, naziv);
            dnevnik.setKomanda("Uspješno vraćeni aerodromi naziva " + naziv + ".");
        } else if (drzava != null && !drzava.isEmpty()) {
            aerodromi = aplikacija2WS.dajAerodromeDrzava(korisnik, lozinka, drzava);
            dnevnik.setKomanda("Uspješno vraćeni aerodromi države " + drzava + ".");
        } else {
            aerodromi = aplikacija2WS.dajAerodromeNaziv(korisnik, lozinka, "%");
            dnevnik.setKomanda("Uspješno vraćeni aerodromi naziva \"%\" (svi aerodromi).");
        }
        dnevnikFacade.create(dnevnik);
        return aerodromi;
    }

    /**
     * Ako je primljeni zahtjev ispravan, šalje JMS poruku u red poruka NWTiS_ikosmerl_2.
     *
     * @param korisnik korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaciju
     * @param komanda komanda koja se šalje u JMS red poruka
     * @param vrijeme vrijeme prijema komande
     * @return odgovor na poslani zahtjev
     */
    @Path("/komande")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response posaljiKomandu(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("komanda") String komanda,
            @QueryParam("vrijeme") String vrijeme) {

        Odgovor odgovor = new Odgovor();
        List<String> praznaLista = new ArrayList<>();

        if (provjeriKorisnickePodatke(korisnik, lozinka, odgovor, praznaLista)) {
            return Response
                    .ok(odgovor)
                    .status(400)
                    .build();
        }

        postKomandeJMSIDnevnik(korisnik, komanda, vrijeme);

        odgovor.setStatus("10");
        odgovor.setPoruka("OK");
        odgovor.setOdgovor(praznaLista.toArray());
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }

    /**
     * Pomoćna metoda koja sadrži glavnu logiku 7. operacije RESTful web servisa.
     *
     * @param korisnik korisničko ime za kreiranje sintakse poruke i zapis u dnevnik
     * @param komanda komanda koja se proslijeđuje u red poruka
     * @param vrijeme vrijeme koje se proslijeđuje u red poruka
     */
    private void postKomandeJMSIDnevnik(String korisnik, String komanda, String vrijeme) {
        String sintaksaPoruke = "{\"id\": \"" + brojac + "\", \"korisnik\": \"" + korisnik + "\", \"komanda\": \"" + komanda
                + "\", \"vrijemePrijema\": \"" + vrijeme + "\", \"vrijemeSlanja\": \""
                + new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS").format(new Date()) + "\"}";
        jmsNWTiSikosmerl2.saljiJMSUDrugiRedPoruka(sintaksaPoruke);

        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setVrijemeZapisa(new Date());
        dnevnik.setKomanda("Uspješno poslana JMS poruka u drugi red poruka.");
        dnevnikFacade.create(dnevnik);
    }

    /**
     * Pomoćna metoda za pretvorbu objekta tipa Airport u Aerodrom.
     *
     * @param airport objekt tipa Airport
     * @return objekt tipa Aerodrom
     */
    private Aerodrom airportsToAerodrom(Airports airport) {
        Lokacija lokacijaAerodrom = new Lokacija();
        String koordinateAerodrom = airport.getCoordinates();
        lokacijaAerodrom.setLatitude(koordinateAerodrom.split(", ")[1]);
        lokacijaAerodrom.setLongitude(koordinateAerodrom.split(", ")[0]);
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setIcao(airport.getIdent());
        aerodrom.setNaziv(airport.getName());
        aerodrom.setDrzava(airport.getIsoCountry());
        aerodrom.setLokacija(lokacijaAerodrom);
        return aerodrom;
    }

    /**
     * Pomoćna metoda za autentikaciju korisnika.
     *
     * @param korisnik korisničko ime korisnika kojeg se autentificira
     * @param lozinka lozinka korisnika kojeg se autentificira
     * @param odgovor objekt tipa Odgovor koji se šalje kao odgovor na zahtjev
     * @param lista lista koja je uvijek prazna jer se vraća kad korisnik nije autenticiran (lista aviona ili aerodroma)
     * @return true ako je korisnik autentificiran, false ako nije
     */
    private boolean provjeriKorisnickePodatke(String korisnik, String lozinka, Odgovor odgovor, List<?> lista) {
        if (korisnik == null || korisnik.isEmpty() || lozinka == null || lozinka.isEmpty()
                || korisniciFacade.autentificirajKorisnika(korisnik, lozinka).isEmpty()) {
            odgovor.setStatus("40");
            odgovor.setPoruka("Korisničko ime ili lozinka su neispravni ili neuneseni!");
            odgovor.setOdgovor(lista.toArray());
            return true;
        }
        return false;
    }
}
