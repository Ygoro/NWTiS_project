/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ikosmerl.ejb.eb;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Igor Košmerl
 */
@Entity
@Table(name = "KORISNICI_GRUPE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "KorisniciGrupe.findAll", query = "SELECT k FROM KorisniciGrupe k")})
public class KorisniciGrupe implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected KorisniciGrupePK korisniciGrupePK;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Boolean status;

    public KorisniciGrupe() {
    }

    public KorisniciGrupe(KorisniciGrupePK korisniciGrupePK) {
        this.korisniciGrupePK = korisniciGrupePK;
    }

    public KorisniciGrupe(KorisniciGrupePK korisniciGrupePK, Boolean status) {
        this.korisniciGrupePK = korisniciGrupePK;
        this.status = status;
    }

    public KorisniciGrupe(String korIme, String grIme) {
        this.korisniciGrupePK = new KorisniciGrupePK(korIme, grIme);
    }

    public KorisniciGrupePK getKorisniciGrupePK() {
        return korisniciGrupePK;
    }

    public void setKorisniciGrupePK(KorisniciGrupePK korisniciGrupePK) {
        this.korisniciGrupePK = korisniciGrupePK;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (korisniciGrupePK != null ? korisniciGrupePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KorisniciGrupe)) {
            return false;
        }
        KorisniciGrupe other = (KorisniciGrupe) object;
        if ((this.korisniciGrupePK == null && other.korisniciGrupePK != null) || (this.korisniciGrupePK != null && !this.korisniciGrupePK.equals(other.korisniciGrupePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.ikosmerl.ejb.eb.KorisniciGrupe[ korisniciGrupePK=" + korisniciGrupePK + " ]";
    }
    
}
