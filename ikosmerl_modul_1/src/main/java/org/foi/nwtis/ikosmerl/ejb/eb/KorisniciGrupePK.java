/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ikosmerl.ejb.eb;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Igor Košmerl
 */
@Embeddable
public class KorisniciGrupePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "KOR_IME", nullable = false, length = 10)
    private String korIme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "GR_IME", nullable = false, length = 10)
    private String grIme;

    public KorisniciGrupePK() {
    }

    public KorisniciGrupePK(String korIme, String grIme) {
        this.korIme = korIme;
        this.grIme = grIme;
    }

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getGrIme() {
        return grIme;
    }

    public void setGrIme(String grIme) {
        this.grIme = grIme;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (korIme != null ? korIme.hashCode() : 0);
        hash += (grIme != null ? grIme.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KorisniciGrupePK)) {
            return false;
        }
        KorisniciGrupePK other = (KorisniciGrupePK) object;
        if ((this.korIme == null && other.korIme != null) || (this.korIme != null && !this.korIme.equals(other.korIme))) {
            return false;
        }
        if ((this.grIme == null && other.grIme != null) || (this.grIme != null && !this.grIme.equals(other.grIme))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.ikosmerl.ejb.eb.KorisniciGrupePK[ korIme=" + korIme + ", grIme=" + grIme + " ]";
    }
    
}
