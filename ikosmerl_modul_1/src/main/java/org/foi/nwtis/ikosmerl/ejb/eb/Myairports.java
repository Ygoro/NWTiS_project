/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ikosmerl.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Igor Košmerl
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"USERNAME", "IDENT"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Myairports.findAll", query = "SELECT m FROM Myairports m")})
public class Myairports implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date stored;
    @JoinColumn(name = "IDENT", referencedColumnName = "IDENT", nullable = false)
    @ManyToOne(optional = false)
    private Airports ident;
    @JoinColumn(name = "USERNAME", referencedColumnName = "KOR_IME", nullable = false)
    @ManyToOne(optional = false)
    private Korisnici username;

    public Myairports() {
    }

    public Myairports(Integer id) {
        this.id = id;
    }

    public Myairports(Integer id, Date stored) {
        this.id = id;
        this.stored = stored;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStored() {
        return stored;
    }

    public void setStored(Date stored) {
        this.stored = stored;
    }

    public Airports getIdent() {
        return ident;
    }

    public void setIdent(Airports ident) {
        this.ident = ident;
    }

    public Korisnici getUsername() {
        return username;
    }

    public void setUsername(Korisnici username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Myairports)) {
            return false;
        }
        Myairports other = (Myairports) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.ikosmerl.ejb.eb.Myairports[ id=" + id + " ]";
    }
    
}
