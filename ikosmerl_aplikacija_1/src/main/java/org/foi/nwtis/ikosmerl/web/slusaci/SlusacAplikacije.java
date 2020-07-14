package org.foi.nwtis.ikosmerl.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.ikosmerl.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.ikosmerl.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ikosmerl.posluzitelji.PosluziteljZahtjeva;

/**
 * Slušač aplikacije ikosmerl_aplikacija_1.
 *
 * @author Igor Košmerl
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    private PosluziteljZahtjeva posluziteljZahtjeva;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            posluziteljZahtjeva = new PosluziteljZahtjeva(konf);
            posluziteljZahtjeva.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija  ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Aplikacija je pokrenuta!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija je zaustavljana!");
        if(posluziteljZahtjeva != null) posluziteljZahtjeva.interrupt();
    }
}
