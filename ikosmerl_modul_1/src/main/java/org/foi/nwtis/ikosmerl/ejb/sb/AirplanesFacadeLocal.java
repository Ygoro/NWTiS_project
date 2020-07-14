package org.foi.nwtis.ikosmerl.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.ikosmerl.ejb.eb.Airplanes;

/**
 * Sučelje koje implementira fasada AirplanesFacade.
 * 
 * @author Igor Košmerl
 */
@Local
public interface AirplanesFacadeLocal {

    void create(Airplanes airplanes);

    void edit(Airplanes airplanes);

    void remove(Airplanes airplanes);

    Airplanes find(Object id);

    List<Airplanes> findAll();

    List<Airplanes> findRange(int[] range);

    int count();
    
    List<Airplanes> vratiPoletjeleAvioneAerodromaUIntervalu(String icao, long vrijemeOd, long vrijemeDo);
    
    List<Airplanes> dajLetoveAvionaUIntervalu(String icao24, long vrijemeOd, long vrijemeDo);
    
    List<Airplanes> dajPoletjeleAvioneAerodroma(String ident);
}
