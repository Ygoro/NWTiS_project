package org.foi.nwtis.ikosmerl.poruke;

import lombok.Getter;
import lombok.Setter;

/**
 * Entitetska klasa za poruke iz drugog reda poruka (NWTiS_ikosmerl_2).
 * 
 * @author Igor Košmerl
 */
public class PorukeNWTiS_ikosmerl_2 {
    
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String komanda;
    @Getter
    @Setter
    private String vrijemePrijema;
    @Getter
    @Setter
    private String vrijemeSlanja;

    public PorukeNWTiS_ikosmerl_2(int id, String korisnik, String komanda, String vrijemePrijema, String vrijemeSlanja) {
        this.id = id;
        this.korisnik = korisnik;
        this.komanda = komanda;
        this.vrijemePrijema = vrijemePrijema;
        this.vrijemeSlanja = vrijemeSlanja;
    }
    
    
    
}
