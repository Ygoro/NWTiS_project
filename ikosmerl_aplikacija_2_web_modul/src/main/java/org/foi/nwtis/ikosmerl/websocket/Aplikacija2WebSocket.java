package org.foi.nwtis.ikosmerl.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.ikosmerl.ejb.eb.Airports;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;
import org.foi.nwtis.ikosmerl.ejb.eb.Korisnici;
import org.foi.nwtis.ikosmerl.ejb.eb.Myairports;
import org.foi.nwtis.ikosmerl.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.ikosmerl.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.ikosmerl.jms.JMSNWTiS_ikosmerl_1;
import org.foi.nwtis.ikosmerl.konfiguracije.Konfiguracija;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * WebSocket krajnja točka za podatake o aerodromima.
 *
 * @author Igor Košmerl
 */
@ServerEndpoint("/aerodromi")
@Stateless
public class Aplikacija2WebSocket {

    @Inject
    ServletContext context;

    @EJB
    MyairportsFacadeLocal myairportsFacade;

    @EJB
    AirportsFacadeLocal airportsFacade;

    @EJB
    KorisniciFacadeLocal korisniciFacade;

    @EJB
    DnevnikFacadeLocal dnevnikFacade;

    @EJB
    JMSNWTiS_ikosmerl_1 jmsNWTiSikosmerl1;

    private BP_Konfiguracija konf;
    private static int brojac = 0;

    static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    public Aplikacija2WebSocket() {
        System.out.println("Websocket pokrenut.");
    }

    @PostConstruct
    public void pokreniDretvu() {
        Konfiguracija konfiguracija = ((BP_Konfiguracija) context.getAttribute("BP_Konfig")).getKonfig();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int brojAerodroma = myairportsFacade.dajAerodromeZaKojeSePreuzimajuPodaci().size();
                send(brojAerodroma + "; " + new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS").format(new Date()));
                System.out.println("Ukupan broj aerodroma za koje se preuzimaju podaci: " + brojAerodroma);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, Integer.parseInt(konfiguracija.dajPostavku("websocket.interval")) * 1000);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        Myairports myairports = new Myairports();
        Airports airport = airportsFacade.find(message.replace("KORISNIK", "").replace("ICAO", "").split(";")[1].trim());
        Korisnici korisnik = korisniciFacade.find(message.replace("KORISNIK", "").replace("ICAO", "").split(";")[0].trim());
        if (airport != null && korisnik != null) {
            myairports.setIdent(airport);
            myairports.setStored(new Date());
            myairports.setUsername(korisnik);
            myairportsFacade.create(myairports);

            Dnevnik dnevnik = new Dnevnik();
            dnevnik.setKomanda("Dodan aerodrom " + myairports.getIdent().getIdent() + " korisniku.");
            dnevnik.setKorisnik(korisnik.getKorIme());
            dnevnik.setVrijemeZapisa(new Date());
            dnevnikFacade.create(dnevnik);

            String sintaksaPoruke = "{\"id\": \"" + brojac
                    + "\", \"korisnik\": \""
                    + message.replace("KORISNIK", "").replace("ICAO", "").split(";")[0].trim()
                    + "\", \"aerodrom\": \""
                    + message.replace("KORISNIK", "").replace("ICAO", "").split(";")[1].trim()
                    + "\", \"vrijeme\": \""
                    + new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS").format(new Date()) + "\"}";
            jmsNWTiSikosmerl1.saljiJMSUPrviRedPoruka(sintaksaPoruke);
        }
    }

    /**
     * Metoda koja šalje danu tekstualnu poruku na WebSocket.
     *
     * @param poruka tekst poruke koja se šalje
     */
    public static void send(String poruka) {
        try {
            for (Session session : queue) {
                session.getBasicRemote().sendText(poruka);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @OnOpen
    public void openConnection(Session session) {
        queue.add(session);
        System.out.println("Otvorena veza.");
    }

    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        System.out.println("Zatvorena veza.");
    }

}
