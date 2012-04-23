/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.dbentities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Family
 */
@Entity
@Table(name = "channeltype", catalog = "SilverTrumpets", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Channeltype.findAll", query = "SELECT c FROM Channeltype c"),
    @NamedQuery(name = "Channeltype.findByIdChannelType", query = "SELECT c FROM Channeltype c WHERE c.idChannelType = :idChannelType"),
    @NamedQuery(name = "Channeltype.findByName", query = "SELECT c FROM Channeltype c WHERE c.name = :name")})
public class Channeltype implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "idChannelType")
    private Integer idChannelType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;

    public Channeltype() {
    }

    public Channeltype(Integer idChannelType) {
        this.idChannelType = idChannelType;
    }

    public Channeltype(Integer idChannelType, String name) {
        this.idChannelType = idChannelType;
        this.name = name;
    }

    public Integer getIdChannelType() {
        return idChannelType;
    }

    public void setIdChannelType(Integer idChannelType) {
        this.idChannelType = idChannelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idChannelType != null ? idChannelType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Channeltype)) {
            return false;
        }
        Channeltype other = (Channeltype) object;
        if ((this.idChannelType == null && other.idChannelType != null) || (this.idChannelType != null && !this.idChannelType.equals(other.idChannelType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pinartek.dbentities.Channeltype[ idChannelType=" + idChannelType + " ]";
    }
    
}
