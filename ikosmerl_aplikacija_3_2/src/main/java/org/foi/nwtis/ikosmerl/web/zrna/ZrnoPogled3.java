package org.foi.nwtis.ikosmerl.web.zrna;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * Zrno s metodama za prikaz i upravljanje svim elementima pogleda 3 treće aplikacije. 
 * Pogled 3 služi za pregled spremljenih MQTT poruka prijavljenog korisnika i daje mogućnost brisanja svih korisnikovih poruka.
 *
 * @author Igor Košmerl
 */
@Named(value = "pogled3")
@ViewScoped
public class ZrnoPogled3 implements Serializable {

    @Inject
    ServletContext context;

    public ZrnoPogled3() {
    }

}
