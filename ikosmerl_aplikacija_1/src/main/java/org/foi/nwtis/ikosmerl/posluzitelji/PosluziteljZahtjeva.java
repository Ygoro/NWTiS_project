package org.foi.nwtis.ikosmerl.posluzitelji;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * Poslužitelj zahtjeva klijenata za preuzimanjem podataka.
 *
 * @author Igor Košmerl
 */
public class PosluziteljZahtjeva extends Thread{

    @Inject
    ServletContext context;
    
    private BP_Konfiguracija bpk;
    private ZahtjevKlijenta dretvaZahtjev;
    private ServerSocket ss;
    private Boolean aktivan = true;

    public PosluziteljZahtjeva(BP_Konfiguracija konf) {
        this.bpk = konf;
    }

    @Override
    public void run() {
        try {
            String port = bpk.getKonfig().dajPostavku("socketserver.port");
            String maksCekaca = bpk.getKonfig().dajPostavku("socketserver.maksCekaca");
            ss = new ServerSocket(Integer.parseInt(port), Integer.parseInt(maksCekaca));
            System.out.println("Čekanje...");
            while (aktivan) {
                dretvaZahtjev = new ZahtjevKlijenta(ss.accept(), bpk);
                dretvaZahtjev.start();
                dretvaZahtjev.join();
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("[Greška] Neuspješno stvaranje socketa!");
        }
    }

    @Override
    public void interrupt() {
        try {
            aktivan = false;
            ss.close();
        } catch (IOException ex) {
            Logger.getLogger(PosluziteljZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
