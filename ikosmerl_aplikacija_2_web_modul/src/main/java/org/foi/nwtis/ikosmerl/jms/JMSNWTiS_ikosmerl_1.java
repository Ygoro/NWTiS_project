package org.foi.nwtis.ikosmerl.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Klasa za slanje JMS poruka u prvi red poruka NWTiS_ikosmerl_1.
 * 
 * @author Igor Košmerl
 */
@Stateless
public class JMSNWTiS_ikosmerl_1 {
    
    @Resource(mappedName = "jms/NWTiS_ikosmerl_1")
    private Queue nWTiS_ikosmerl_1;
    
    @Resource(mappedName = "jms/NWTiS_QF_ikosmerl_1")
    private ConnectionFactory nWTiS_QF_ikosmerl_1;
    
    /**
     * Šalje poruku u red poruka NWTiS_ikosmerl_1.
     * 
     * @param poruka poruka koja se šalje
     */
    public void saljiJMSUPrviRedPoruka(String poruka) {
        try {
            saljiJMSPoruku(poruka);
        } catch (JMSException | NamingException ex) {
            Logger.getLogger(JMSNWTiS_ikosmerl_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Kreira JMS poruku u application/json formatu.
     * 
     * @param session
     * @param messageData
     * @return
     * @throws JMSException 
     */
    private Message kreirajJMSPoruku(Session session, Object messageData) throws JMSException {
        TextMessage tm = session.createTextMessage();
        tm.setJMSType("application/json");
        tm.setText(messageData.toString());
        return tm;
    }

    private void saljiJMSPoruku(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        Connection conn = null;
        Session s = null;
        try {
            conn= nWTiS_QF_ikosmerl_1.createConnection();
            s = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer mp = s.createProducer(nWTiS_ikosmerl_1);
            mp.send(kreirajJMSPoruku(s, messageData));
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
}
