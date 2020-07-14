package org.foi.nwtis.ikosmerl.poruke;

import lombok.Getter;
import lombok.Setter;

/**
 * Entitetska klasa za poruke iz prvog reda poruka (NWTiS_ikosmerl_1).
 * 
 * @author Igor Košmerl
 */
public class PorukeNWTiS_ikosmerl_1 {
    
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String aerodrom;
    @Getter
    @Setter
    private String vrijeme;
    
    public PorukeNWTiS_ikosmerl_1(int id, String korisnik, String aerodrom, String vrijeme) {
        this.id = id;
        this.korisnik = korisnik;
        this.aerodrom = aerodrom;
        this.vrijeme = vrijeme;
    }
    
}
