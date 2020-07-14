package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.ejb.eb.Dnevnik;
import org.foi.nwtis.ikosmerl.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.ikosmerl.konfiguracije.bp.BP_Konfiguracija;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 4 treće aplikacije.
 * Pogled 4 služi prikazivanju dnevnika prijavljenog korisnika.
 * 
 * @author Igor Košmerl
 */
@Named(value = "pogled4")
@ViewScoped
public class ZrnoPogled4 implements Serializable{
    
    @EJB
    DnevnikFacadeLocal dnevnikFacade;
    
    @Inject
    ZrnoPogled1 pogled1;

    @Getter
    @Setter
    private int brojLinijaPoStranici;
    @Getter
    @Setter
    private String korime;
    
    @Inject
    ServletContext context;
    
    public ZrnoPogled4() {
    }
    
    /**
     * Definira prijavljenog korisnika
     * Puni listu podataka dnevnika prijavljenog korisnika njegovim zapisima u dnevniku.
     */
    @PostConstruct
    public void postaviPodatkePogleda4() {
        BP_Konfiguracija konf = (BP_Konfiguracija) context.getAttribute("BP_Konfig");
        brojLinijaPoStranici = Integer.parseInt(konf.getKonfig().dajPostavku("app3.pogled4.brojLinijaPoStranici"));
    }
    
    public List<Dnevnik> popuniListuDnevnika() {
        return dnevnikFacade.dajPodatkeDnevnikaKorisnika(pogled1.getKorisnik());
    }
    
}
