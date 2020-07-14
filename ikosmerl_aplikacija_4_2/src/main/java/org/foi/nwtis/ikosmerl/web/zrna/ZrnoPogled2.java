package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.poruke.PorukeNWTiS_ikosmerl_1;
import org.foi.nwtis.ikosmerl.poruke.PorukeNWTiS_ikosmerl_2;
import org.foi.nwtis.ikosmerl.sb.SpremisteJMS;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 2 četvrte aplikacije.
 * Pogled 2 služi za prikazivanje JMS redova poruka.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled2")
@ViewScoped
public class ZrnoPogled2 implements Serializable {
    
    @EJB
    SpremisteJMS spremisteJMSPoruka;
    
    @Inject
    ZrnoPogled1 pogled1;
    
    @Getter
    @Setter
    private List<PorukeNWTiS_ikosmerl_1> listaPorukaPrvogReda = new ArrayList<>();
    @Getter
    @Setter
    private List<PorukeNWTiS_ikosmerl_2> listaPorukaDrugogReda = new ArrayList<>();
    
    public ZrnoPogled2() {
    }
    
    /**
     * Popunjava listu za prikaz poruka prvog reda poruka
     * 
     * @return listu poruka prvog reda poruka
     */
    public List<PorukeNWTiS_ikosmerl_1> popuniListuPorukaPrvogReda() {
        for(PorukeNWTiS_ikosmerl_1 porukaPrvogReda : spremisteJMSPoruka.getPorukePrvogReda()) {
            if(porukaPrvogReda.getKorisnik().equals(pogled1.getKorisnik())) {
                listaPorukaPrvogReda.add(porukaPrvogReda);
            }
        }
        return listaPorukaPrvogReda;
    }
    
    /**
     * Popunjava listu za prikaz poruka drugog reda poruka
     * 
     * @return listu poruka drugog reda poruka
     */
    public List<PorukeNWTiS_ikosmerl_2> popuniListuPorukaDrugogReda() {
        for(PorukeNWTiS_ikosmerl_2 porukaDrugogReda : spremisteJMSPoruka.getPorukeDrugogReda()) {
            if(porukaDrugogReda.getKorisnik().equals(pogled1.getKorisnik())) {
                listaPorukaDrugogReda.add(porukaDrugogReda);
            }
        }
        return listaPorukaDrugogReda;
    }
    
    /**
     * Briše JMS poruke oba reda poruka.
     * 
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public String obrisiJMSPoruke() {
        List<PorukeNWTiS_ikosmerl_1> listaPrvihPoruka = new ArrayList<>();
        List<PorukeNWTiS_ikosmerl_2> listaDrugihPoruka = new ArrayList<>();
        for(PorukeNWTiS_ikosmerl_1 porukaPrvogReda : spremisteJMSPoruka.getPorukePrvogReda()) {
            if(!porukaPrvogReda.getKorisnik().equals(pogled1.getKorisnik())) {
                listaPrvihPoruka.add(porukaPrvogReda);
            }
        }
        for(PorukeNWTiS_ikosmerl_2 porukaDrugogReda : spremisteJMSPoruka.getPorukeDrugogReda()) {
            if(!porukaDrugogReda.getKorisnik().equals(pogled1.getKorisnik())) {
                listaDrugihPoruka.add(porukaDrugogReda);
            }
        }
        spremisteJMSPoruka.setPorukePrvogReda(listaPrvihPoruka);
        spremisteJMSPoruka.setPorukeDrugogReda(listaDrugihPoruka);
        listaPorukaPrvogReda = listaPrvihPoruka;
        listaPorukaDrugogReda = listaDrugihPoruka;
        return "";
    }

}
