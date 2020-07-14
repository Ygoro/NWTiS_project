package org.foi.nwtis.ikosmerl.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.ikosmerl.poruke.PorukeNWTiS_ikosmerl_1;
import org.foi.nwtis.ikosmerl.poruke.PorukeNWTiS_ikosmerl_2;

/**
 * Klasa za spremanje JMS poruka.
 * 
 * @author Igor Košmerl
 */
@Startup
@Singleton
public class SpremisteJMS {
    
    @Getter
    @Setter
    private List<PorukeNWTiS_ikosmerl_1> porukePrvogReda = new ArrayList<>();
    @Getter
    @Setter
    private List<PorukeNWTiS_ikosmerl_2> porukeDrugogReda = new ArrayList<>();
    
    /**
     * Dodaje poruku iz porvog reda poruka u listu.
     * 
     * @param poruka poruka koja se dodaje u listu
     */
    public void dodajPorukuPrvogReda(PorukeNWTiS_ikosmerl_1 poruka) {
        porukePrvogReda.add(poruka);
    }
    
    /**
     * Dodaje poruku iz drugog reda poruka u listu.
     * 
     * @param poruka poruka koja se dodaje u listu
     */
    public void dodajPorukuDrugogReda(PorukeNWTiS_ikosmerl_2 poruka) {
        porukeDrugogReda.add(poruka);
    }
    
}
