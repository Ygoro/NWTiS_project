package org.foi.nwtis.ikosmerl.ws.serveri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;
import org.foi.nwtis.ikosmerl.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 * JAX-WS SOAP web servis (drugi zadatak druge aplikacije).
 * Zahtjev svake operacije upisuje se u dnevnik.
 *
 * @author Igor Košmerl
 */
@WebService(serviceName = "Aplikacija2")
public class Aplikacija2 {

    @EJB
    AirportsFacadeLocal airportsFacade;

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @EJB
    AirplanesFacadeLocal airplanesFacade;

    @EJB
    DnevnikFacadeLocal dnevnikFacade;

    /**
     * Autentificira korisnika koji se služi operacijama servisa.
     *
     * @param korime korisničko ime za autentikaciju
     * @param lozinka lozinka za autentikaci
     * @return true ako je korisnik uspješno autentificiran (false ako nije)
     */
    @WebMethod(operationName = "autentificirajKorisnika")
    public List<Korisnici> autentificirajKorisnika(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka) {
        return korisniciFacade.autentificirajKorisnika(korime, lozinka);
    }

    /**
     * Vraća popis aerodroma koji imaju sličan naziv koji se traži.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param naziv traženi naziv aerodroma
     * @return ako je korisnik autenticiran, lista aerodroma ako postoje aerodromi traženog naziva, inače null
     */
    @WebMethod(operationName = "dajAerodromeNaziv")
    public List<Aerodrom> dajAerodromeNaziv(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "naziv") String naziv) {
        List<Korisnici> korisnik = korisniciFacade.autentificirajKorisnika(korime, lozinka);
        if (!korisnik.isEmpty()) {
            List<Aerodrom> listaAerodroma = new ArrayList<>();
            for (Airports airport : airportsFacade.vratiAerodromeNaziva(naziv)) {
                listaAerodroma.add(airportsToAerodrom(airport));
            }
            Dnevnik dnevnik = new Dnevnik();
            dnevnik.setKomanda("Zatraženi aerodromi naziva " + naziv + ".");
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return listaAerodroma;
        }
        return null;
    }

    /**
     * Vraća popis aerodroma koji su iz određene države.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param kodDrzave kod tražene država aerodroma
     * @return ako je korisnik autenticiran, lista aerodroma ako postoje aerodromi tražene države, inače null
     */
    @WebMethod(operationName = "dajAerodromeDrzava")
    public List<Aerodrom> dajAerodromeDrzava(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "kodDrzave") String kodDrzave) {
        List<Korisnici> korisnik = korisniciFacade.autentificirajKorisnika(korime, lozinka);
        if (!korisnik.isEmpty()) {
            List<Aerodrom> listaAerodroma = new ArrayList<>();
            for (Airports airport : airportsFacade.vratiAerodromeDrzave(kodDrzave)) {
                listaAerodroma.add(airportsToAerodrom(airport));
            }
            Dnevnik dnevnik = new Dnevnik();
            dnevnik.setKomanda("Zatraženi aerodromi države " + kodDrzave + ".");
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return listaAerodroma;
        }
        return null;
    }

    /**
     * Vraća popis svih aerodroma koje prati korisnik.
     *
     * @param korime korisničko ime za autentikaciju i pretragu aerodroma korisnika
     * @param lozinka lozinka za autentikaciju i pretragu aerodroma korisnika
     * @return ako je korisnik autenticiran, lista aerodroma korisnika ako postoje, inače null
     */
    @WebMethod(operationName = "dajAerodromeKorisnika")
    public List<Aerodrom> dajAerodromeKorisnika(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka) {
        List<Korisnici> korisnik = korisniciFacade.autentificirajKorisnika(korime, lozinka);
        if (!korisnik.isEmpty()) {
            List<Aerodrom> listaAerodroma = new ArrayList<>();
            for (Airports airport : airportsFacade.vratiAerodromeKorisnika(korisnik.get(0))) {
                listaAerodroma.add(airportsToAerodrom(airport));
            }
            Dnevnik dnevnik = new Dnevnik();
            dnevnik.setKomanda("Zatraženi aerodromi korisnika.");
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return listaAerodroma;
        }
        return null;
    }

    /**
     * Vraća popis svih aviona koji su polijetali s traženog aerodroma i lete u zadanom vremenskom intervalu.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param icao oznaka aerodroma s kojeg su poletjeli avioni
     * @param vrijemeOd vrijeme od kojeg se pretražuju letovi (UNIX timestamp)
     * @param vrijemeDo vrijeme do kojeg se pretražuju letovi (UNIX timestamp)
     * @return ako je korisnik autenticiran, lista poletjelih aviona zadanog intervala s zadanog aerodroma ako postoje, inače null
     */
    @WebMethod(operationName = "dajPoletjeleAvioneAerodroma")
    public List<AvionLeti> dajPoletjeleAvioneAerodroma(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "vrijemeOd") long vrijemeOd,
            @WebParam(name = "vrijemeDo") long vrijemeDo) {
        if (!korisniciFacade.autentificirajKorisnika(korime, lozinka).isEmpty()) {
            List<AvionLeti> poletjeliAvioni = new ArrayList<>();
            for (Airplanes airplane : airplanesFacade.vratiPoletjeleAvioneAerodromaUIntervalu(icao, vrijemeOd, vrijemeDo)) {
                airplanesToAvionLeti(airplane, poletjeliAvioni);
            }
            Dnevnik dnevnik = new Dnevnik();
            Date vrijemeOdCitljivo = new Date(vrijemeOd * 1000);
            Date vrijemeDoCitljivo = new Date(vrijemeDo * 1000);
            dnevnik.setKomanda("Zatraženi poletjeli avioni aerodroma " + icao + " u intervalu: " + vrijemeOdCitljivo + " - " + vrijemeDoCitljivo);
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return poletjeliAvioni;
        }
        return null;
    }

    /**
     * Vraća popis letova izabranog aviona u određenom razdoblju.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param icao24 oznaka aviona za koji se traže letovi
     * @param vrijemeOd vrijeme od kojeg se pretražuju letovi (UNIX timestamp)
     * @param vrijemeDo vrijeme do kojeg se pretražuju letovi (UNIX timestamp)
     * @return ako je korisnik autenticiran, lista letova traženog aviona u traženom razdoblju ako postoje, inače null
     */
    @WebMethod(operationName = "dajLetoveAviona")
    public List<AvionLeti> dajLetoveAviona(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao24") String icao24,
            @WebParam(name = "vrijemeOd") long vrijemeOd,
            @WebParam(name = "vrijemeDo") long vrijemeDo) {
        if (!korisniciFacade.autentificirajKorisnika(korime, lozinka).isEmpty()) {
            List<AvionLeti> poletjeliAvioni = new ArrayList<>();
            for (Airplanes airplane : airplanesFacade.dajLetoveAvionaUIntervalu(icao24, vrijemeOd, vrijemeDo)) {
                airplanesToAvionLeti(airplane, poletjeliAvioni);
            }
            Dnevnik dnevnik = new Dnevnik();
            Date vrijemeOdCitljivo = new Date(vrijemeOd * 1000);
            Date vrijemeDoCitljivo = new Date(vrijemeDo * 1000);
            dnevnik.setKomanda("Zatraženi letovi aviona " + icao24 + " u intervalu: " + vrijemeOdCitljivo + " - " + vrijemeDoCitljivo);
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return poletjeliAvioni;
        }
        return null;
    }

    /**
     * Vraća udaljenost u km između dva izabrana aerodroma.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param prviIcao oznaka prvog aerodroma od kojeg se mjeri udaljenost
     * @param drugiIcao oznaka drugog aerodroma do kojeg se mjeri udaljenost
     * @return ako je korisnik autenticiran, udaljenost u km ako postoje aerodromi traženih oznaka, inače null
     */
    @WebMethod(operationName = "dajUdaljenostAerodroma")
    public int dajUdaljenostAerodroma(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "prviIcao") String prviIcao,
            @WebParam(name = "drugiIcao") String drugiIcao) {
        if (!korisniciFacade.autentificirajKorisnika(korime, lozinka).isEmpty()) {
            Airports prviAerodrom = airportsFacade.find(prviIcao);
            Airports drugiAerodrom = airportsFacade.find(drugiIcao);
            //provjera ne funkcionira (ne razumijem zašto)
            if (prviAerodrom != null || drugiAerodrom != null) {
                String koordinatePrvogAerodroma = prviAerodrom.getCoordinates();
                String koordinateDrugogAerodroma = drugiAerodrom.getCoordinates();
                double latPrviAerodrom = Double.parseDouble(koordinatePrvogAerodroma.split(", ")[1]);
                double lonPrviAerodrom = Double.parseDouble(koordinatePrvogAerodroma.split(", ")[0]);
                double latDrugiAerodrom = Double.parseDouble(koordinateDrugogAerodroma.split(", ")[1]);
                double lonDrugiAerodrom = Double.parseDouble(koordinateDrugogAerodroma.split(", ")[0]);
                Dnevnik dnevnik = new Dnevnik();
                dnevnik.setKomanda("Zatražena udaljenost između aerodroma " + prviIcao + " i " + drugiIcao + ".");
                dnevnik.setKorisnik(korime);
                dnevnik.setVrijemeZapisa(new Date());
                dnevnikFacade.create(dnevnik);
                return izracunajUdaljenostKoordinata(latDrugiAerodrom, latPrviAerodrom, lonDrugiAerodrom, lonPrviAerodrom);
            }
            return 0;
        }
        return 0;
    }

    /**
     * Vraća popis svih aerodroma korisnika koji su udaljeni od unesenog aerodroma unutar unesenih granica u km.
     *
     * @param korime korisničko ime za autentikaciju korisnika
     * @param lozinka lozinka za autentikaciju korisnika
     * @param icao oznaka aerodroma korisnika
     * @param donjaGranica donja granica udaljenosti od traženog aerodroma
     * @param gornjaGranica gornja granica udaljenosti od traženog aerodroma
     * @return ako je korisnik autenticiran, lista aerodroma tražene oznake u traženim granicama udaljenosti ako postoje, inače null
     */
    @WebMethod(operationName = "dajAerodromeKorisnikaUnutarGranica")
    public List<Aerodrom> dajAerodromeKorisnikaUnutarGranica(
            @WebParam(name = "korime") String korime,
            @WebParam(name = "lozinka") String lozinka,
            @WebParam(name = "icao") String icao,
            @WebParam(name = "donjaGranica") int donjaGranica,
            @WebParam(name = "gornjaGranica") int gornjaGranica) {
        List<Korisnici> korisnik = korisniciFacade.autentificirajKorisnika(korime, lozinka);
        if (korisnik != null) {
            Airports trazeniAerodrom = airportsFacade.find(icao);
            List<Airports> listaSvihAerodromaKorisnika = new ArrayList<>();
            for (Airports airport : airportsFacade.vratiAerodromeKorisnika(korisnik.get(0))) {
                listaSvihAerodromaKorisnika.add(airport);
            }
            List<Aerodrom> listaAerodromaUnutarGranica = new ArrayList<>();
            for (Airports aerodrom : listaSvihAerodromaKorisnika) {
                String koordinateTrazenogAerodroma = trazeniAerodrom.getCoordinates();
                String koordinateTrenutnogAerodroma = aerodrom.getCoordinates();
                double latTrazeniAer = Double.parseDouble(koordinateTrazenogAerodroma.split(", ")[1]);
                double lonTrazeniAer = Double.parseDouble(koordinateTrazenogAerodroma.split(", ")[0]);
                double latTrenutniAer = Double.parseDouble(koordinateTrenutnogAerodroma.split(", ")[1]);
                double lonTrenutniAer = Double.parseDouble(koordinateTrenutnogAerodroma.split(", ")[0]);
                int udaljenost = izracunajUdaljenostKoordinata(latTrenutniAer, latTrazeniAer, lonTrenutniAer, lonTrazeniAer);
                if (udaljenost >= donjaGranica && udaljenost <= gornjaGranica) {
                    listaAerodromaUnutarGranica.add(airportsToAerodrom(aerodrom));
                }
            }
            Dnevnik dnevnik = new Dnevnik();
            dnevnik.setKomanda("Zatraženi aerodromi korisnika unutar granica udaljenosti: " + 
                    donjaGranica + "km i " + gornjaGranica + "km od aerodroma " + icao + ".");
            dnevnik.setKorisnik(korime);
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);
            return listaAerodromaUnutarGranica;
        }
        return null;
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

    /**
     * Pomoćna metoda za pretvorbu objekta tipa Airplanes u AvionLeti.
     *
     * @param airplane objekt tipa Airplanes koji predstavlja jedan avion originalne liste (u svakoj iteraciji nadređene petlje)
     * @param poletjeliAvioni lista objakata AvionLeti koju se puni konvertiranim podacima
     */
    private void airplanesToAvionLeti(Airplanes airplane, List<AvionLeti> poletjeliAvioni) {
        String icaoAvion = airplane.getIcao24();
        int firstseenAvion = airplane.getFirstseen();
        String estDepartureAirportAvion = airplane.getEstdepartureairport().getIdent();
        int lastseenAvion = airplane.getLastseen();
        String estArrivalAirportAvion = airplane.getEstarrivalairport();
        String callsignAvion = airplane.getCallsign();
        int estDepartureAirportHorizDistanceAvion = airplane.getEstdepartureairporthorizdistance();
        int estDepartureAirportVertDistanceAvion = airplane.getEstdepartureairportvertdistance();
        int estArrivalAirportHorizDistanceAvion = airplane.getEstarrivalairporthorizdistance();
        int estArrivalAirportVertDistanceAvion = airplane.getEstarrivalairportvertdistance();
        int departureAirportCandidatesCountAvion = airplane.getDepartureairportcandidatescount();
        int arrivalAirportCandidatesCountAvion = airplane.getArrivalairportcandidatescount();
        AvionLeti poletjeliAvion = new AvionLeti(icaoAvion, firstseenAvion, estDepartureAirportAvion, lastseenAvion,
                estArrivalAirportAvion, callsignAvion, estDepartureAirportHorizDistanceAvion,
                estDepartureAirportVertDistanceAvion, estArrivalAirportHorizDistanceAvion,
                estArrivalAirportVertDistanceAvion, departureAirportCandidatesCountAvion,
                arrivalAirportCandidatesCountAvion);
        poletjeliAvioni.add(poletjeliAvion);
    }

    /**
     * Pomoćna metoda za izračun udaljenosti između dviju geografskih koordinata, korištenjem trigonometrijskih funkcija.
     *
     * @param latDrugiAerodrom prva koordinata drugog aerodroma
     * @param latPrviAerodrom prva koordinata prvog aerodroma
     * @param lonDrugiAerodrom druga koordinata drugog aerodroma
     * @param lonPrviAerodrom druga koordinata prvog aerodroma
     * @return udaljenost između geografskih koordinata u kilometrima
     */
    private int izracunajUdaljenostKoordinata(double latDrugiAerodrom, double latPrviAerodrom,
            double lonDrugiAerodrom, double lonPrviAerodrom) {
        double radiusZemljeM = 6371000;
        double latUdaljenost = Math.toRadians(latDrugiAerodrom - latPrviAerodrom);
        double lonUdaljenost = Math.toRadians(lonDrugiAerodrom - lonPrviAerodrom);
        double a = Math.sin(latUdaljenost / 2) * Math.sin(latUdaljenost / 2)
                + Math.cos(Math.toRadians(latPrviAerodrom)) * Math.cos(Math.toRadians(latDrugiAerodrom))
                * Math.sin(lonUdaljenost / 2) * Math.sin(lonUdaljenost / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (radiusZemljeM * c / 1000);
    }
}
