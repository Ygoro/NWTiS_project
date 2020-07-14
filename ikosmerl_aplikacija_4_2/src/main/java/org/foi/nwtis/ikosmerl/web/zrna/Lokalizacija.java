package org.foi.nwtis.ikosmerl.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 * Zrno za upravljanje lokalizacijom (postavljanje jezika).
 * 
 * @author Igor Košmerl
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {
    
    @Inject
    private FacesContext facesContext;
    
    @Getter
    @Setter
    private String jezik = "hr";
    
    public Lokalizacija() {
    }
    
    /**
     * Metoda za postavljanje jezika.
     * 
     * @return prazan String kako bi se ostalo na istoj putanji nakon izvršavanja metode
     */
    public Object odaberiJezik() {
        facesContext.getViewRoot().setLocale(new Locale(jezik));
        return "";
    }
    
}
