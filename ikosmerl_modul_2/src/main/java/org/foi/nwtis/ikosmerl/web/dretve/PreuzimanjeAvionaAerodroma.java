package org.foi.nwtis.ikosmerl.web.dretve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairportslog;
import org.foi.nwtis.ikosmerl.ejb.eb.MyairportslogPK;
import org.foi.nwtis.ikosmerl.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.ikosmerl.konfiguracije.Konfiguracija;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

@Stateless
public class PreuzimanjeAvionaAerodroma extends Thread {

    @EJB
    AirportsFacadeLocal airportsFacade;

    @EJB
    MyairportslogFacadeLocal myairportslogFacade;

    @EJB
    AirplanesFacadeLocal airplanesFacade;

    private Airplanes airplanes;
    private Myairportslog myairportslog;
    private MyairportslogPK myairportslogPK;
    private List<AvionLeti> poletjeliAvioni;
    private OSKlijent osKlijent;
    private String osKorime;
    private String osLozinka;
    private String osDatumPocetkaPreuzimanja;
    private String osDatumKrajaPreuzimanja;
    private BP_Konfiguracija konf;
    private int intervalCiklusa;
    private int pauzaPreuzimanja;
    private int socketPort;
    private String socketServer;
    private String preuzimanjeKorisnik;
    private String preuzimanjeLozinka;

    @Setter
    private boolean krajRada = false;

    public PreuzimanjeAvionaAerodroma() {
    }

    /**
     * Pomoćna metoda za postavljanje jedinstvene konfiguracije. Nije implementirano preko konstruktora, jer se na tja način ne može
     * dohvatiti u slušaču prve aplikacije.
     *
     * @param konf konfiguracija
     */
    public void postaviKonfiguraciju(BP_Konfiguracija konf) {
        this.konf = konf;
    }

    /**
     * Metoda koja se izvršava pozivom prekida rada dretve.
     */
    @Override
    public void interrupt() {
        krajRada = true;
        super.interrupt();
    }

    /**
     * Metoda koja se izvršava pokretanjem dretve. Pokretanjem dretve rade se razne provjere nakon čega je moguće pokrenuti
     * preuzimanje letova aviona aerodroma.
     */
    @Override
    public void run() {
        osKlijent = new OSKlijent(osKorime, osLozinka);
        long epochPocetak = 0;
        try {
            epochPocetak = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(osDatumPocetkaPreuzimanja + " 00:00:00").getTime() / 1000;
        } catch (ParseException ex) {
            Logger.getLogger(PreuzimanjeAvionaAerodroma.class.getName()).log(Level.SEVERE, null, ex);
        }
        long epochKraj = epochPocetak + 86400;
        while (!krajRada) {
            List<Airports> aerodromiKorisnika = airportsFacade.vratiAerodromeSvihKorisnika();
            preuzmiAerodrome(aerodromiKorisnika, epochPocetak, epochKraj);
            epochPocetak += 86400;
            epochKraj += 86400;
            provjeriPauzaKraj(epochPocetak);
        }
    }

    /**
     * Glavna metoda za preuzimanje podataka. Nakon provjeravanja da li je dozvoljeno preuzimanje, poziva druge pomoćne metode za
     * provjeru i zapisivanje podataka u slučaju uspješnog preuzimanja letova aviona pojedinog aerodroma.
     *
     * @param aerodromiKorisnika lista aerodroma korisnika koji se dohvaćaju metodom fasade
     * @param epochPocetak epoch vrijednost datuma i vremena pocetka
     * @param epochKraj epoch vrijednost datuma i vremena kraja
     * @throws ParseException iznimka koja se dohvaća mogućim neispravnim parsiranjem datuma kod provjeravanja pauze ili kraja rada
     * @throws InterruptedException iznimka koja se dohvaća mogućim neispravnim prekidanjem rada
     */
    private void preuzmiAerodrome(List<Airports> aerodromiKorisnika, long epochPocetak, long epochKraj) {
        try {
            String odgovor = provjeriSmijeLiPreuzeti();
            if (odgovor.equals("OK 11;")) {
                for (Airports trenutniAerodrom : aerodromiKorisnika) {
                    Date pocetakSamoDatum = new Date(epochPocetak * 1000);
                    if (myairportslogFacade.provjeriPostojiAerodrom(trenutniAerodrom.getIdent(), pocetakSamoDatum).isEmpty()) {
                        poletjeliAvioni = osKlijent.getDepartures(trenutniAerodrom.getIdent(), epochPocetak, epochKraj);
                        provjeriKreirajAvionLeti();
                        zapisiOdradenoPreuzimanje(trenutniAerodrom, pocetakSamoDatum);
                        System.out.println("Uspješno zapisani svi avioni aerodroma " + trenutniAerodrom.getName() + ".");
                    }
                    System.out.println("Spavanje: " + pauzaPreuzimanja + "ms.");
                    Thread.sleep(pauzaPreuzimanja);
                }
            }
            System.out.println("Spavanje između ciklusa: " + intervalCiklusa + "s.");
            Thread.sleep(intervalCiklusa * 1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Pomoćna metoda za provjeru pauze ili kraja rada dretve.
     *
     * @param epoch epoch vrijednost vremena početka iz konfiguracije
     */
    private void provjeriPauzaKraj(long epoch) {
        try {
            if (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(osDatumKrajaPreuzimanja + " 00:00:00").getTime() / 1000
                    <= System.currentTimeMillis() / 1000) {
                try {
                    System.out.println("Spavanje: 1 dan.");
                    Thread.sleep(86400 * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeAvionaAerodroma.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (epoch >= new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                    .parse(osDatumKrajaPreuzimanja + " 00:00:00").getTime() / 1000 + 86400) {
                krajRada = true;
                System.out.println("Kraj rada: " + krajRada);
            }
        } catch (ParseException ex) {
            Logger.getLogger(PreuzimanjeAvionaAerodroma.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pomoćna metoda za zapis podataka o odrađenom preuzimanju u tablicu MYAIRPORTSLOG (korištenjem fasade).
     *
     * @param trenutniAerodrom trenutni aerodrom za kojeg u se uspješno preuzeli svi podaci o letovima aviona
     * @param pocetakSamoDatum datum pocetka iz konfiguracije
     */
    private void zapisiOdradenoPreuzimanje(Airports trenutniAerodrom, Date pocetakSamoDatum) {
        myairportslog = new Myairportslog();
        myairportslog.setAirports(trenutniAerodrom);
        myairportslogPK = new MyairportslogPK(trenutniAerodrom.getIdent(), pocetakSamoDatum);
        myairportslog.setMyairportslogPK(myairportslogPK);
        myairportslog.setStored(new Date());
        myairportslogFacade.create(myairportslog);
    }

    /**
     * Pomoćna metoda za kreiranje objekta tipa Airplanes i njegovo zapisivanje u bezu korištenjem metode create fasade. Prije
     * zapisa potrebno je provjeriti da li avion leti tako da se provjeri postoji li njegov zapis za aerodrom slijetanja.
     */
    private void provjeriKreirajAvionLeti() {
        for (AvionLeti poletjeliAvion : poletjeliAvioni) {
            if (poletjeliAvion.getEstArrivalAirport() != null) {
                airplanes = new Airplanes();
                airplanes.setIcao24(poletjeliAvion.getIcao24());
                airplanes.setFirstseen(poletjeliAvion.getFirstSeen());
                airplanes.setCallsign(poletjeliAvion.getCallsign());
                airplanes.setLastseen(poletjeliAvion.getLastSeen());
                airplanes.setArrivalairportcandidatescount(poletjeliAvion.getArrivalAirportCandidatesCount());
                airplanes.setDepartureairportcandidatescount(poletjeliAvion.getDepartureAirportCandidatesCount());
                airplanes.setEstarrivalairport(poletjeliAvion.getEstArrivalAirport());
                airplanes.setEstarrivalairporthorizdistance(poletjeliAvion.getEstArrivalAirportHorizDistance());
                airplanes.setEstarrivalairportvertdistance(poletjeliAvion.getEstArrivalAirportVertDistance());
                airplanes.setEstdepartureairport(airportsFacade.find(poletjeliAvion.getEstDepartureAirport()));
                airplanes.setEstdepartureairporthorizdistance(poletjeliAvion.getEstDepartureAirportHorizDistance());
                airplanes.setEstdepartureairportvertdistance(poletjeliAvion.getEstDepartureAirportVertDistance());
                airplanes.setStored(new Date());
                airplanesFacade.create(airplanes);
            }
        }
    }

    /**
     * Inicijalna provjera o smjelosti preuzimanja, povezivanjem na aplikaciju 1.
     *
     * @return odgovor poslužitelja o dozvoljenosti preuzimanja podataka (prazan string ako se ne smije preuzimati ili je došlo do
     * kraja preuzimanja
     */
    private String provjeriSmijeLiPreuzeti() {
        try {
            Socket veza = new Socket(socketServer, socketPort);
            OutputStreamWriter pisacIzlaznogToka = new OutputStreamWriter(veza.getOutputStream());
            InputStream ulazniTok = veza.getInputStream();
            pisacIzlaznogToka.write("KORISNIK " + preuzimanjeKorisnik + "; LOZINKA " + preuzimanjeLozinka + "; STANJE;");
            pisacIzlaznogToka.flush();
            veza.shutdownOutput();
            BufferedReader br = new BufferedReader(new InputStreamReader(ulazniTok, "UTF-8"));
            String odgovor = br.readLine();
            veza.shutdownInput();
            veza.close();
            return odgovor;
        } catch (IOException ex) {
            krajRada = true;
            System.out.println(ex.getMessage());
        }

        return "";
    }

    /**
     * Metoda koja pokreće dretvu i preuzima podatke iz konfiguracije.
     */
    @Override
    public synchronized void start() {
        Konfiguracija konfiguracija = konf.getKonfig();
        osKorime = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik");
        osLozinka = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka");
        osDatumPocetkaPreuzimanja = konfiguracija.dajPostavku("preuzimanje.pocetak");
        osDatumKrajaPreuzimanja = konfiguracija.dajPostavku("preuzimanje.kraj");
        intervalCiklusa = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.ciklus"));
        pauzaPreuzimanja = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.pauza"));
        socketPort = Integer.parseInt(konfiguracija.dajPostavku("socketserver.port"));
        socketServer = konfiguracija.dajPostavku("socketserver.server");
        preuzimanjeKorisnik = konfiguracija.dajPostavku("preuzimanje.korisnik");
        preuzimanjeLozinka = konfiguracija.dajPostavku("preuzimanje.lozinka");
        super.start();
    }

}
