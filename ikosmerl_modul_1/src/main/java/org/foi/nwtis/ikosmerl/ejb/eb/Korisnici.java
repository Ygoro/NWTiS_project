/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ikosmerl.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Igor Košmerl
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Korisnici.findAll", query = "SELECT k FROM Korisnici k")})
public class Korisnici implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "KOR_IME", nullable = false, length = 10)
    private String korIme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(nullable = false, length = 25)
    private String ime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(nullable = false, length = 25)
    private String prezime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(nullable = false, length = 20)
    private String lozinka;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "EMAIL_ADRESA", nullable = false, length = 40)
    private String emailAdresa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATUM_KREIRANJA", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumKreiranja;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATUM_PROMJENE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumPromjene;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "username")
    private List<Myairports> myairportsList;

    public Korisnici() {
    }

    public Korisnici(String korIme) {
        this.korIme = korIme;
    }

    public Korisnici(String korIme, String ime, String prezime, String lozinka, String emailAdresa, Date datumKreiranja, Date datumPromjene) {
        this.korIme = korIme;
        this.ime = ime;
        this.prezime = prezime;
        this.lozinka = lozinka;
        this.emailAdresa = emailAdresa;
        this.datumKreiranja = datumKreiranja;
        this.datumPromjene = datumPromjene;
    }

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getEmailAdresa() {
        return emailAdresa;
    }

    public void setEmailAdresa(String emailAdresa) {
        this.emailAdresa = emailAdresa;
    }

    public Date getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(Date datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public Date getDatumPromjene() {
        return datumPromjene;
    }

    public void setDatumPromjene(Date datumPromjene) {
        this.datumPromjene = datumPromjene;
    }

    @XmlTransient
    public List<Myairports> getMyairportsList() {
        return myairportsList;
    }

    public void setMyairportsList(List<Myairports> myairportsList) {
        this.myairportsList = myairportsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (korIme != null ? korIme.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Korisnici)) {
            return false;
        }
        Korisnici other = (Korisnici) object;
        if ((this.korIme == null && other.korIme != null) || (this.korIme != null && !this.korIme.equals(other.korIme))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.ikosmerl.ejb.eb.Korisnici[ korIme=" + korIme + " ]";
    }
    
}
