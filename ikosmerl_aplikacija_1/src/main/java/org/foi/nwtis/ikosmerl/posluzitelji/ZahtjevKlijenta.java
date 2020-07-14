package org.foi.nwtis.ikosmerl.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ikosmerl.podaci.KorisnikDAO;
import org.foi.nwtis.ikosmerl.rest.klijenti.Aplikacija3RS;
import org.foi.nwtis.ikosmerl.ws.klijenti.IkosmerlAerodromiWS;
import org.foi.nwtis.ikosmerl.ws.klijenti.StatusKorisnika;
import org.foi.nwtis.podaci.OdgovorAerodrom;

/**
 * Klasa dretve koja obrađuje zahtjev klijenta dobiven na poslužitelju zahtjeva.
 *
 * @author Igor Košmerl
 */
public class ZahtjevKlijenta extends Thread {

    private IkosmerlAerodromiWS ikosmerlAerodromiWS = new IkosmerlAerodromiWS();
    private final Socket veza;
    private KorisnikDAO korisnikDAO;
    private String korime;
    private String lozinka;
    private Aplikacija3RS aplikacija3RS;
    private Boolean autentificiran;
    private BP_Konfiguracija bpk;
    private static Boolean stanjeAktivno = true;
    private String ikosmerlKorime;
    private String ikosmerlLozinka;

    ZahtjevKlijenta(Socket veza, BP_Konfiguracija bpk) {
        this.veza = veza;
        this.bpk = bpk;
        this.korisnikDAO = new KorisnikDAO();
    }

    /**
     * Metoda koja se izvršava pokretanjem dretve.
     */
    @Override
    public void run() {
        try (
                InputStream ulazniTok = veza.getInputStream();
                OutputStream izlazniTok = veza.getOutputStream();
                OutputStreamWriter pisacIzlaznogToka = new OutputStreamWriter(izlazniTok);) {

            BufferedReader br = new BufferedReader(new InputStreamReader(ulazniTok, "UTF-8"));
            String unesenaKomanda = br.readLine();
            if (unesenaKomanda != null) {
                ikosmerlKorime = bpk.getKonfig().dajPostavku("admin.username");
                ikosmerlLozinka = bpk.getKonfig().dajPostavku("admin.password");
                korime = unesenaKomanda.split(";")[0].split(" ")[1];
                lozinka = unesenaKomanda.split(";")[1].trim().split(" ")[1];
                autentificiran = korisnikDAO.autentificirajKorisnika(korime, lozinka, bpk);
                provjeriOdaberiKomandu(pisacIzlaznogToka, unesenaKomanda);
                izlazniTok.flush();
            }
            upisiKomanduUDnevnik(unesenaKomanda);
            String trenutnoVrijeme = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS").format(new Date());
            aplikacija3RS = new Aplikacija3RS(korime, lozinka);
            aplikacija3RS.posaljiKomandu(OdgovorAerodrom.class, unesenaKomanda, trenutnoVrijeme);
            System.out.println(unesenaKomanda);
        } catch (IOException ex) {
            System.out.println("[Greška] Nemoguće otvaranje toka podataka!");
            System.exit(0);
        }
    }

    /**
     * Pomoćna metoda za primanje zahtjeva i njegov upis u dnevnik.
     *
     * @param tekst unesenaKomanda zahtjeva
     */
    private void upisiKomanduUDnevnik(String tekst) {
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setKorisnik(korime);
        dnevnik.setKomanda(tekst);
        dnevnik.setVrijemeZapisa(new Date());
    }

    /**
     * Provjerava valjanost komande poslužitelja korištenjem dopuštenih izraza, a ako komanda nije prepoznata poziva metodu za
     * provjeru komandi grupe. Autentikacija korisnika uvjet je za izvršavanje svih komandi osim za komande DODAJ.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje informacija o mogućoj greški
     * @param ulazniTekst komanda koju je unio klijent
     */
    private void provjeriOdaberiKomandu(OutputStreamWriter pisacIzlaznogToka, String ulazniTekst) {
        String sintaksaKorisnikLozinka = "^KORISNIK .\\S+; LOZINKA .\\S+;";
        if (!Pattern.compile(sintaksaKorisnikLozinka + " DODAJ;").matcher(ulazniTekst).matches() && autentificiran) {
            if (Pattern.compile(sintaksaKorisnikLozinka).matcher(ulazniTekst).matches()) {
                ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 10;");
            } else if (Pattern.compile(sintaksaKorisnikLozinka + " PAUZA;$").matcher(ulazniTekst).matches()) {
                izvrsiPauzuPreuzimanja(pisacIzlaznogToka);
            } else if (Pattern.compile(sintaksaKorisnikLozinka + " RADI;$").matcher(ulazniTekst).matches()) {
                izvrsiNastaviRad(pisacIzlaznogToka);
            } else if (Pattern.compile(sintaksaKorisnikLozinka + " KRAJ;$").matcher(ulazniTekst).matches()) {
                izvrsiPrekiniPreuzimanje(pisacIzlaznogToka);
            } else if (Pattern.compile(sintaksaKorisnikLozinka + " STANJE;$").matcher(ulazniTekst).matches()) {
                izvrsiDajStanje(pisacIzlaznogToka);
            }
        } else if (Pattern.compile(sintaksaKorisnikLozinka + " DODAJ;$").matcher(ulazniTekst).matches()) {
            izvrsiDodajKorisnika(pisacIzlaznogToka, ulazniTekst);
        } else {
            provjeriOdaberiKomanduGrupe(pisacIzlaznogToka, ulazniTekst, sintaksaKorisnikLozinka);
        }
    }

    /**
     * Provjerava valjanost komande za grupu korištenjem dopuštenih izraza. Autentikacija korisnika uvjet je za izvršavanje svih
     * komandi.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje informacija o mogućoj greški
     * @param ulazniTekst komanda koju je unio klijent
     * @param sintaksaKorLoz sintaksa osnovne komande za autentikaciju na koju se nadodaju sintakse ostalih komandi za provjeru
     */
    private void provjeriOdaberiKomanduGrupe(OutputStreamWriter pisacIzlaznogToka, String ulazniTekst, String sintaksaKorLoz) {
        String sintaksaGrupaAerodromi = sintaksaKorLoz + " GRUPA AERODROMI ((\\w+)(,\\s{1}\\w+)*);";
        if (Pattern.compile(sintaksaKorLoz + " GRUPA PRIJAVI;$").matcher(ulazniTekst).matches()) {
            izvrsiPrijaviGrupu(pisacIzlaznogToka);
        } else if (Pattern.compile(sintaksaKorLoz + " GRUPA ODJAVI;$").matcher(ulazniTekst).matches()) {
            izvrsiOdjaviGrupu(pisacIzlaznogToka);
        } else if (Pattern.compile(sintaksaKorLoz + " GRUPA AKTIVIRAJ;$").matcher(ulazniTekst).matches()) {
            izvrsiAktivirajGrupu(pisacIzlaznogToka);
        } else if (Pattern.compile(sintaksaKorLoz + " GRUPA BLOKIRAJ;$").matcher(ulazniTekst).matches()) {
            izvrsiBlokirajGrupu(pisacIzlaznogToka);
        } else if (Pattern.compile(sintaksaKorLoz + " GRUPA STANJE;$").matcher(ulazniTekst).matches()) {
            izvrsiDajStatusGrupe(pisacIzlaznogToka);
        } else if (Pattern.compile(sintaksaGrupaAerodromi).matcher(ulazniTekst).matches()) {
            String icaoOznake = ulazniTekst.split(";")[2].replace(" GRUPA AERODROMI ", "");
            izvrsiPostaviAerodrome(pisacIzlaznogToka, icaoOznake);
        } else {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 11;");
        }
    }

    /**
     * Dodaje korisnika u bazu podataka pozivom metode za dodavanje korisnika klase KorisnikDAO.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     * @param ulazniTekst komanda koju je unio klijent
     */
    private void izvrsiDodajKorisnika(OutputStreamWriter pisacIzlaznogToka, String ulazniTekst) {
        String korime = ulazniTekst.split(";")[0].split(" ")[1];
        String lozinka = ulazniTekst.split(";")[1].trim().split(" ")[1];
        if (korisnikDAO.dodajKorisnika(korime, lozinka, bpk)) {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 10;");
        } else {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 12;");
        }
    }

    /**
     * Vraća OK 11; odgovor ako je poslužitelj prije pauze bio aktivan ili ERR 13; ako je poslužitelj prije pauze bio pasivan. Osim
     * toga postavlja vrijednost varijable stanja na false, tj. pauzira preuzimanje.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiPauzuPreuzimanja(OutputStreamWriter pisacIzlaznogToka) {
        if (stanjeAktivno == true) {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 10;");
        } else {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 13;");
        }
        stanjeAktivno = false;
    }

    /**
     * Vraća OK 11; odgovor ako je poslužitelj prije pauze bio pasivan ili ERR 13; ako je poslužitelj prije pauze bio aktivan. Osim
     * toga postavlja vrijednost varijable stanja na true, tj. ponovo pokreće preuzimanje.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiNastaviRad(OutputStreamWriter pisacIzlaznogToka) {
        if (stanjeAktivno == false) {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 10;");
        } else {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 14;");
        }
        stanjeAktivno = true;
    }

    /**
     * Potpuno prekida preuzimanje podataka tako da poziva metodu za prekid rada dretve za preuzimanje podataka. Osim toga,
     * kompletno prekida preuzimanje komandi. Vraća poruku OK 10;
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiPrekiniPreuzimanje(OutputStreamWriter pisacIzlaznogToka) {
        ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 10;");
        System.exit(0);
    }

    /**
     * Vraća OK 11; odgovor ako je trenutno poslužitelj aktivan ili OK 12; ako je trenutno poslužitelj pasivan.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiDajStanje(OutputStreamWriter pisacIzlaznogToka) {
        if (stanjeAktivno == true) {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 11;");
        } else {
            ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 12;");
        }
    }

    /**
     * Vraća OK 20; odgovor ako grupa nije bila registrirana i u tom slučaju je registrira (prijavljuje) ili ERR 20; ako je bila
     * registrirana.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiPrijaviGrupu(OutputStreamWriter pisacIzlaznogToka) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case DEREGISTRIRAN:
                case NEPOSTOJI:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 20;");
                    ikosmerlAerodromiWS.registrirajGrupu(ikosmerlKorime, ikosmerlLozinka);
                    break;
                case AKTIVAN:
                case NEAKTIVAN:
                case BLOKIRAN:
                case PASIVAN:
                case REGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 20;");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Vraća OK 20; odgovor ako je grupa bila registrirana i u tom slučaju je deregistrira (odjavljuje) ili ERR 21; ako nije bila
     * registrirana.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiOdjaviGrupu(OutputStreamWriter pisacIzlaznogToka) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case REGISTRIRAN:
                case AKTIVAN:
                case NEAKTIVAN:
                case BLOKIRAN:
                case PASIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 20;");
                    ikosmerlAerodromiWS.deregistrirajGrupu(ikosmerlKorime, ikosmerlLozinka);
                    break;
                case DEREGISTRIRAN:
                case NEPOSTOJI:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 21;");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Poziva metodu servisa za aktivaciju grupe ako je grupa neaktivna i ispisuje OK 20;, ako je već bila aktivna vraća se poruka
     * ERR 22;, ako grupa ne postoji vraća se poruka ERR 21;.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiAktivirajGrupu(OutputStreamWriter pisacIzlaznogToka) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case NEAKTIVAN:
                case PASIVAN:
                case BLOKIRAN:
                case REGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 20;");
                    ikosmerlAerodromiWS.aktivirajGrupu(ikosmerlKorime, ikosmerlLozinka);
                    break;
                case AKTIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 22;");
                    break;
                case NEPOSTOJI:
                case DEREGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 21;");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Poziva metodu servisa za blokiranje grupe ako je grupa aktivna i ispisuje OK 20;, ako je neaktivna vraća se poruka ERR 23;,
     * ako grupa ne postoji vraća se poruka ERR 21;. Za sve ostale statuse vraća ERR naziv_statusa.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiBlokirajGrupu(OutputStreamWriter pisacIzlaznogToka) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case AKTIVAN:
                case REGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 20;");
                    ikosmerlAerodromiWS.blokirajGrupu(ikosmerlKorime, ikosmerlLozinka);
                    break;
                case NEAKTIVAN:
                case BLOKIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 23;");
                    break;
                case DEREGISTRIRAN:
                case NEPOSTOJI:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 21;");
                    break;
                case PASIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR PASIVAN;");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Ako je status grupe AKTIVAN vraća poruku OK 21;, ako je status grupe BLOKIRAN vraća OK 22;, ako je status grupe NEPOSTOJI
     * vraća ERR 21;. Za sve ostale statuse vraća ERR naziv_statusa.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     */
    private void izvrsiDajStatusGrupe(OutputStreamWriter pisacIzlaznogToka) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case AKTIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 21;");
                    break;
                case BLOKIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 22;");
                    break;
                case NEPOSTOJI:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 21;");
                    break;
                case PASIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR PASIVAN;");
                    break;
                case NEAKTIVAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR NEAKTIVAN;");
                    break;
                case REGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR REGISTRIRAN;");
                    break;
                case DEREGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR DEREGISTRIRAN;");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Ako je grupa blokirana, brišu se svi postojeći aerodromi i postavljaju se novi (uneseni komandom) i vraća poruka OK 20;, ako
     * grupa nije blokirana vraća se poruka ERR 31;, ako postoji neki drugi problem vraća se poruka ERR 32;.
     *
     * @param pisacIzlaznogToka izlazni tok podataka na koji se ispisuje odgovor na komandu
     * @param popisIcaoOznakaKomande grupa dopuštenog izraza komande GRUPA AERODROMI koja se odnosi na popis unesenih icao oznaka
     * novih aerodroma
     */
    private void izvrsiPostaviAerodrome(OutputStreamWriter pisacIzlaznogToka, String popisIcaoOznakaKomande) {
        StatusKorisnika status = ikosmerlAerodromiWS.dajStatusGrupe(ikosmerlKorime, ikosmerlLozinka);
        if (status != null) {
            switch (status) {
                case BLOKIRAN:
                    ikosmerlAerodromiWS.obrisiSveAerodromeGrupe(ikosmerlKorime, ikosmerlLozinka);
                    for (String icao : popisIcaoOznakaKomande.split(", ")) {
                        ikosmerlAerodromiWS.dodajAerodromIcaoGrupi(ikosmerlKorime, ikosmerlLozinka, icao);
                    }
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "OK 20;");
                    break;
                case AKTIVAN:
                case PASIVAN:
                case NEAKTIVAN:
                case NEPOSTOJI:
                case REGISTRIRAN:
                case DEREGISTRIRAN:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 31;");
                    break;
                default:
                    ispisiNaIzlazniTok(pisacIzlaznogToka, "ERR 32;");
                    break;
            }
        }
    }

    /**
     * Ispisuje grešku na izlazni tok.
     *
     * @param pisacIzlaznogToka izlazni tok na koji se ispisuje greška
     * @param tekst unesenaKomanda greške (jedan od ERROR-a)
     */
    private void ispisiNaIzlazniTok(OutputStreamWriter pisacIzlaznogToka, String tekst) {
        try {
            pisacIzlaznogToka.write(tekst);
        } catch (IOException ex) {
            System.out.println("[Greška] Nemoguće ispisivanje na izlazni tok!");
        }
    }

}
