package com.foilen.crm.db.entities.invoice;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.AbstractBasics;

/**
 * Technical support contract.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class TechnicalSupport extends AbstractBasics {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(unique = true, nullable = false, length = 10)
    private String sid;
    // 1099 for 10.99$
    private long pricePerHour = 0;

    public TechnicalSupport() {
    }

    public TechnicalSupport(String sid, long pricePerHour) {
        this.sid = sid;
        this.pricePerHour = pricePerHour;
    }

    public Long getId() {
        return id;
    }

    public long getPricePerHour() {
        return pricePerHour;
    }

    public String getSid() {
        return sid;
    }

    public long getVersion() {
        return version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPricePerHour(long pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}
