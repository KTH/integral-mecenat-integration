/*
 * MIT License
 *
 * Copyright (c) 2018 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.integral.mecenat.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * Representation of Mecenat CSV format, see documentation in Mecenat-Filspec-CSV-2018.pdf
 */
@CsvRecord(separator = ";")
public class MecenatCSVRecord {
    @DataField(pos = 1, length = 12, required = true)
    private String personnummer;

    @DataField(pos = 2, trim = true, length = 50, clip = true, required = true)
    private String efternamn;

    @DataField(pos = 3, trim = true, length = 50, clip = true, required = true)
    private String fornamn;

    @DataField(pos = 4, trim = true, length = 50, clip = true)
    private String conamn;

    @DataField(pos = 5, trim = true, length = 50, clip = true)
    private String gatuadress;

    @DataField(pos = 6, trim = true, length = 10, clip = true)
    private String postnummer;

    @DataField(pos = 7, trim = true, length = 50, clip = true)
    private String ort;

    @DataField(pos = 8, trim = true, length = 50, clip = true)
    private String land;

    @DataField(pos = 9, trim = true, length = 50, clip = true)
    private String epost;

    @DataField(pos = 10, length = 10, pattern = "yyyy-MM-dd", required = true)
    private Date studieperiodStart;

    @DataField(pos = 11, length = 10, pattern = "yyyy-MM-dd", required = true)
    private Date studiePeriodSlut;

    @DataField(pos = 12, length = 3, required = true, precision = 0)
    private BigDecimal studieomfattning;

    @DataField(pos = 13, length = 1, defaultValue = "0", required = true)
    private int karmedlem;

    @DataField(pos = 14, length = 30, trim = true, clip = true)
    private String sektion;

    @DataField(pos = 15, length = 30, trim = true, clip = true)
    private String fritext1;

    @DataField(pos = 16, length = 30, trim = true, clip = true)
    private String fritext2;

    @DataField(pos = 17, length = 30, trim = true, clip = true)
    private String fritext3;

    @DataField(pos = 18, length = 5, required = true)
    private String termin;

    public String getPersonnummer() {
        return personnummer;
    }

    public void setPersonnummer(String personnummer) {
        this.personnummer = personnummer;
    }

    public String getEfternamn() {
        return efternamn;
    }

    public void setEfternamn(String efternamn) {
        this.efternamn = efternamn;
    }

    public String getFornamn() {
        return fornamn;
    }

    public void setFornamn(String fornamn) {
        this.fornamn = fornamn;
    }

    public String getConamn() {
        return conamn;
    }

    public void setConamn(String conamn) {
        this.conamn = conamn;
    }

    public String getGatuadress() {
        return gatuadress;
    }

    public void setGatuadress(String gatuadress) {
        this.gatuadress = gatuadress;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getLand() {
        return land;
    }

    /*
     *  Ska vara tom om land är Sverige, se setLand().
     */
    public void setLand(String land) {
        if (! land.trim().equalsIgnoreCase("SVERIGE")) {
            this.land = land;
        }
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public Date getStudieperiodStart() {
        return studieperiodStart;
    }

    public void setStudieperiodStart(Date studieperiodStart) {
        this.studieperiodStart = studieperiodStart;
    }

    public Date getStudiePeriodSlut() {
        return studiePeriodSlut;
    }

    public void setStudieperiodSlut(Date studiePeriodSlut) {
        this.studiePeriodSlut = studiePeriodSlut;
    }

    public BigDecimal getStudieomfattning() {
        return studieomfattning;
    }

    /*
     *  Siffra 0-100, man kan inte ha mer än 100, se setStudieomfattning().
     */
    public void setStudieomfattning(BigDecimal studieomfattning) {
        this.studieomfattning = BigDecimal.valueOf(Math.min(100, studieomfattning.doubleValue()));
    }

    public int getKarmedlem() {
        return karmedlem;
    }

    public void setKarmedlem(int karmedlem) {
        this.karmedlem = karmedlem;
    }

    public String getSektion() {
        return sektion;
    }

    public void setSektion(String sektion) {
        this.sektion = sektion;
    }

    public String getFritext1() {
        return fritext1;
    }

    public void setFritext1(String fritext1) {
        this.fritext1 = fritext1;
    }

    public String getFritext2() {
        return fritext2;
    }

    public void setFritext2(String fritext2) {
        this.fritext2 = fritext2;
    }

    public String getFritext3() {
        return fritext3;
    }

    public void setFritext3(String fritext3) {
        this.fritext3 = fritext3;
    }

    public String getTermin() {
        return termin;
    }

    public void setTermin(String termin) {
        this.termin = termin;
    }
}
