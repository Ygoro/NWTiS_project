package org.foi.nwtis.ikosmerl.mdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.foi.nwtis.ikosmerl.poruke.PorukeNWTiS_ikosmerl_2;
import org.foi.nwtis.ikosmerl.sb.SpremisteJMS;

/**
 * Slušač za poruke iz drugog reda poruka.
 * 
 * @author Igor Košmerl
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_ikosmerl_2"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class SlusacNWTiS_ikosmerl_2 implements MessageListener {

    @Inject
    SpremisteJMS spremisteJMSPoruka;

    public SlusacNWTiS_ikosmerl_2() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            String poruka = message.getBody(String.class);
            JsonObject json = new JsonParser().parse(poruka).getAsJsonObject();
            int id = json.get("id").getAsInt();
            String korisnik = json.get("korisnik").getAsString();
            String komanda = json.get("komanda").getAsString();
            String vrijemePrijema = json.get("vrijemePrijema").getAsString();
            String vrijemeSlanja = json.get("vrijemeSlanja").getAsString();
            PorukeNWTiS_ikosmerl_2 objektPoruka = new PorukeNWTiS_ikosmerl_2(id,korisnik,komanda,vrijemePrijema,vrijemeSlanja);
            spremisteJMSPoruka.dodajPorukuDrugogReda(objektPoruka);
        } catch (JMSException ex) {
            ex.getMessage();
        }
    }
}
