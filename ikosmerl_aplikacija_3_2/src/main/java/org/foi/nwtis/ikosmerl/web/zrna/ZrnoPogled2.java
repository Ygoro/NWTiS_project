package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 2 treće aplikacije. Pogled 2 služi za slanje željenih komandi na
 * poslužitelj prve aplikacije.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled2")
@ViewScoped
public class ZrnoPogled2 implements Serializable {

    @Getter
    @Setter
    private String stanjePosluzitelja;
    @Getter
    @Setter
    private String stanjeGrupe;
    @Getter
    @Setter
    private String prikazOdgovor;
    @Getter
    @Setter
    private List<String> oznakeAerodroma = new ArrayList<>();
    @Getter
    @Setter
    private String uneseniIcao;
    
    private String korisnikGrupa;
    private String lozinkaGrupa;
    private int port;
    private String server;

    @Inject
    ServletContext context;
    
    @Inject
    ZrnoPogled1 pogled1;

    public ZrnoPogled2() {
    }
    
    /**
     * Pomoćna metoda za postavljanje podataka iz konfivuracije u varijable (port te korisničko ime i lozinka za komande grupe)
     * 
     * @throws NumberFormatException 
     */
    @PostConstruct
    private void postaviPodatkeKonfiguracije() throws NumberFormatException {
        BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        port = Integer.parseInt(konf.getKonfig().dajPostavku("socketserver.port"));
        server = konf.getKonfig().dajPostavku("socketserver.server");
        korisnikGrupa = konf.getKonfig().dajPostavku("admin.username");
        lozinkaGrupa = konf.getKonfig().dajPostavku("admin.password");
    }

    /**
     * Pomoćna metoda za povezivanje na poslužitelj aplikacije 1, prosljeđivanje unesene komande i vraćanje odgovora poslužitelja.
     * Na početku metode potrebni podaci preuzimaju se iz konfiguracije.
     *
     * @param komanda unesena komanda
     * @return odgovor poslužitelja prve aplikacije
     */
    public String poveziDajOdgovor(String komanda) {
        try (
                Socket veza = new Socket(server, port);
                OutputStreamWriter pisacIzlaznogToka = new OutputStreamWriter(veza.getOutputStream());
                InputStream is = veza.getInputStream();) {
            pisacIzlaznogToka.write(komanda);
            pisacIzlaznogToka.flush();
            veza.shutdownOutput();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String odgovorPosluzitelja = br.readLine();
            veza.shutdownInput();
            return odgovorPosluzitelja;
        } catch (IOException ex) {
            System.out.println("[Greška] Neuspješno spajanje na poslužitelja aplikacije 1!");
        }
        return "";
    }

    /**
     * Metoda upravljanje odgovorom nakon dohvaćanja trenutnog stanja poslužitelja. OK 11; ako preuzima podatke za aerodrome, OK 12;
     * ako ne preuzima podatke za aerodrome.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajStatusPosluzitelja() {
        String odgovor = poveziDajOdgovor("KORISNIK " + pogled1.getKorisnik() + "; LOZINKA " + pogled1.getLozinka() + "; STANJE;");
        if (odgovor.equals("OK 11;")) {
            prikazOdgovor = "Preuzimaju se podaci za aerodrome.";
            stanjePosluzitelja = "Preuzimaju se podaci za aerodrome";
        } else if (odgovor.equals("OK 12;")) {
            prikazOdgovor = "Ne preuzimaju se podaci za aerodrome.";
            stanjePosluzitelja = "Ne preuzimaju se podaci za aerodrome";
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja pauze preuzimanja podataka za aerodrome. OK 10; ako je poslužitelj bio u
     * aktivnom radu, ERR 13; ako je poslužitelj bio u pasivnom radu.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String izvrsiPauzuPreuzimanja() {
        String odgovor = poveziDajOdgovor("KORISNIK " + pogled1.getKorisnik() + "; LOZINKA " + pogled1.getLozinka() + "; PAUZA;");
        if (odgovor.equals("OK 10;")) {
            prikazOdgovor = "Pauzirano preuzimanje. Poslužitelj je bio u aktivnom radu.";
        } else if (odgovor.equals("ERR 13;")) {
            prikazOdgovor = "Pauzirano preuzimanje. Poslužitelj je bio u pasivnom radu.";
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja nastavnka rada preuzimanja podataka za aerodrome. OK 10; ako je poslužitelj
     * bio u pasivnom radu, ERR 14; ako je poslužitelj bio u aktivnom radu.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String izvrsiNastaviPreuzimanje() {
        String odgovor = poveziDajOdgovor("KORISNIK " + pogled1.getKorisnik() + "; LOZINKA " + pogled1.getLozinka() + "; RADI;");
        if (odgovor.equals("OK 10;")) {
            prikazOdgovor = "Nastavljeno preuzimanje. Poslužitelj je bio u pasivnom radu.";
        } else if (odgovor.equals("ERR 14;")) {
            prikazOdgovor = "Nastavljeno preuzimanje. Poslužitelj je bio u aktivnom radu.";
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja kraja rada preuzimanja podataka za aerodrome. Uvijek vraća odgovor OK 10;
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String izvrsiKrajPreuzimanja() {
        String odgovor = poveziDajOdgovor("KORISNIK " + pogled1.getKorisnik() + "; LOZINKA " + pogled1.getLozinka() + "; KRAJ;");
        prikazOdgovor = "Potpuno prekinuto preuzimanje podataka za aerodrome i preuzimanje komandi.";
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za vraćanje stanja grupe. OK 21; ako je grupa trenutno aktivna, OK
     * 22; ako je grupa trenutno blokirana ili ERR 21; ako grupa ne postoji pa se ne može očitati njeno trenutno stanje.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dajStanjeGrupe() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa + "; GRUPA STANJE;");
        switch(odgovor){
            case "OK 21;":
                prikazOdgovor = "Grupa je trenutno aktivna.";
                stanjeGrupe = "Grupa je trenutno aktivna";
                break;
            case "OK 22;":
                prikazOdgovor = "Grupa je trenutno blokirana.";
                stanjeGrupe = "Grupa je trenutno blokirana";
                break;
            case "ERR 21;":
                prikazOdgovor = "Grupa ne postoji.";
                stanjeGrupe = "Grupa ne postoji";
                break;
            case "ERR PASIVAN;":
                stanjeGrupe = "Grupa je trenutno pasivna";
                break;
            case "ERR NEAKTIVAN;":
                stanjeGrupe = "Grupa je trenutno neaktivna";
                break;
            case "ERR REGISTRIRAN;":
                stanjeGrupe = "Grupa je trenutno registrirana";
                break;
            case "ERR DEREGISTRIRAN;":
                stanjeGrupe = "Grupa je trenutno deregistrirana";
                break;
            default:
                break;
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za registriranje (prijavljivanje) grupe. OK 20; ako grupa do sad
     * nije bila registrirana, ERR 20; ako je grupa već bila registrirana.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String registrirajGrupu() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa + "; GRUPA PRIJAVI;");
        if (odgovor.equals("OK 20;")) {
            prikazOdgovor = "Grupa je uspješno registrirana.";
        } else if (odgovor.equals("ERR 20;")) {
            prikazOdgovor = "Grupa je već registrirana.";
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za odjavljivanje grupe. OK 20; ako je grupa do sad bila
     * registrirana, ERR 21; ako grupa do sad nije bila registrirana.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String odjaviGrupu() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa + "; GRUPA ODJAVI;");
        if (odgovor.equals("OK 20;")) {
            prikazOdgovor = "Grupa je uspješno odjavljena (do sada je bila registrirana).";
        } else if (odgovor.equals("ERR 21;")) {
            prikazOdgovor = "Grupa nije bila registrirana.";
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za aktiviranje grupe. OK 20; ako grupa do sad nije bila aktivna,
     * ERR 22; ako je grupa do sad bila aktivna ili ERR 21; ako grupa ne postoji pa ju je nemoguće aktivirati.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String aktivirajGrupu() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa + "; GRUPA AKTIVIRAJ;");
        switch(odgovor){
            case "OK 20;":
                prikazOdgovor = "Grupa uspješno aktivirana (do sad nije bila aktivna).";
                stanjeGrupe = "Grupa je trenutno aktivna";
                break;
            case "ERR 22;":
                prikazOdgovor = "Grupa je već aktivna, pa ju je nemoguće ponovno aktivirati.";
                break;
            case "ERR 21;":
                prikazOdgovor = "Grupa ne postoji ili nije registrirana, pa ju je nemoguće aktivirati.";
                break;
            default:
                break;
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za blokiranje grupe. OK 20; ako je grupa do sad bila aktivna, ERR
     * 23; ako grupa do sad nije bila aktivna ili ERR 21; ako grupa ne postoji pa ju je nemoguće blokirati.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String blokirajGrupu() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa + "; GRUPA BLOKIRAJ;");
        switch(odgovor){
            case "OK 20;":
                prikazOdgovor = "Grupa uspješno blokirana (do sad je bila aktivna).";
                stanjeGrupe = "Grupa je trenutno blokirana";
                break;
            case "ERR 23;":
                prikazOdgovor = "Grupa nije aktivna, pa ju je nemoguće blokirati.";
                break;
            case "ERR 21;":
                prikazOdgovor = "Grupa ne postoji, pa ju je nemoguće blokirati.";
                break;
            case "ERR PASIVAN;":
                stanjeGrupe = "Grupa je trenutno pasivna";
                break;
            default:
                break;
        }
        return "";
    }

    /**
     * Metoda za upravljanje odgovorom nakon izvršavanja komande za postavljanje novih aerodroma grupe. OK 20; ako su uspješno
     * postavljeni novi aerodromi grupe, ERR 31; ako grupa nije blokirana ili ERR 32; ako je došlo do nekog drugog problema.
     *
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String postaviNoveAerodromeGrupe() {
        String odgovor = poveziDajOdgovor("KORISNIK " + korisnikGrupa + "; LOZINKA " + lozinkaGrupa 
                + "; GRUPA AERODROMI " + String.join(", ", oznakeAerodroma) + ";");
        if (odgovor.equals("OK 20;")) {
            prikazOdgovor = "Uspješno su postavljeni novi aerodromi grupe: " + String.join(", ", oznakeAerodroma) + ".";
        } else if (odgovor.equals("ERR 23;")) {
            prikazOdgovor = "Grupa nije blokirana pa je nemoguće postaviti nove aerodrome grupe.";
        } else if (odgovor.equals("ERR 21;")) {
            prikazOdgovor = "Došlo je do problema kod postavljanja novih aerodroma grupe.";
        }
        return "";
    }
    
    /**
     * Metoda koja pritiskom na gumb za dodavanje aerodroma grupi, dodaje aerodrom u listu aerodroma koji će se dodati grupi.
     * 
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String dodajAerodromGrupe() {
        oznakeAerodroma.clear();
        oznakeAerodroma.add(uneseniIcao);
        return "";
    }

}
