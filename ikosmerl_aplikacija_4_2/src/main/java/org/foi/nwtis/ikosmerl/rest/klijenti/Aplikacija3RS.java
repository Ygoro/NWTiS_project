package org.foi.nwtis.ikosmerl.rest.klijenti;

import java.text.MessageFormat;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:Aplikacija3RestAerodromi [aerodromi]<br>
 * USAGE:
 * <pre>
 *        Aplikacija3RS client = new Aplikacija3RS();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Igor Košmerl
 */
public class Aplikacija3RS {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/ikosmerl_aplikacija_3_1/webresources";

    public Aplikacija3RS() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public Response dodajAerodromKorisniku(Object requestEntity) throws ClientErrorException {
        return webTarget
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T dajAerodome(Class<T> responseType, String drzava, String naziv) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (drzava != null) {
            resource = resource.queryParam("drzava", drzava);
        }
        if (naziv != null) {
            resource = resource.queryParam("naziv", naziv);
        }
        resource = resource.path("svi");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response obrisiPracenjeAerodromaKorisnika(String korime, String lozinka, String icao) throws ClientErrorException {
        return webTarget
                .path(MessageFormat.format("{0}", new Object[]{icao}))
                .request(MediaType.APPLICATION_JSON)
                .header("korisnik", korime)
                .header("lozinka", lozinka)
                .delete(Response.class);
    }

    public Response obrisiLetoveAvionaAerodromaKorisnika(String korime, String lozinka, String icao) throws ClientErrorException {
        return webTarget
                .path(MessageFormat.format("{0}/avioni", new Object[]{icao}))
                .request(MediaType.APPLICATION_JSON)
                .header("korisnik", korime)
                .header("lozinka", lozinka)
                .delete(Response.class);
    }

    public Response posaljiKomandu(Object requestEntity) throws ClientErrorException {
        return webTarget
                .path("komande").request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    public <T> T dajAerodomeKorisnika(String korime, String lozinka, Class<T> responseType) throws ClientErrorException {
        return webTarget
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header("korisnik", korime)
                .header("lozinka", lozinka)
                .get(responseType);
    }

    public <T> T dajAerodromKojiPratiKorisnik(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{icao}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.close();
    }

}
