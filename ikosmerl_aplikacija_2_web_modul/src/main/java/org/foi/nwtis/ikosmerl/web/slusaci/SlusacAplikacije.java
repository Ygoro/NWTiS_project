package org.foi.nwtis.ikosmerl.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.ikosmerl.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.ikosmerl.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ikosmerl.web.dretve.PreuzimanjeAvionaAerodroma;

/**
 * Slušač aplikacije ikosmerl_aplikacija_2.
 * 
 * @author Igor Košmerl
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
    
    @EJB
    PreuzimanjeAvionaAerodroma preuzimanjeLetovaAvionaAerodroma;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            preuzimanjeLetovaAvionaAerodroma.postaviKonfiguraciju(konf);
            preuzimanjeLetovaAvionaAerodroma.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Aplikacija je pokrenuta!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplikacija je zaustavljana!");
    }
}
