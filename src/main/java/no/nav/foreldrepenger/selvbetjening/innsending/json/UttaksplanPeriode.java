package no.nav.foreldrepenger.selvbetjening.innsending.json;

import java.util.List;

public class UttaksplanPeriode {
    public String type;
    public String årsak;
    public String konto;
    public String orgnr;
    public Boolean erArbeidstaker;
    public Double stillingsprosent;
    public Boolean ønskerSamtidigUttak;
    public Boolean gradert;
    public String morsAktivitetIPerioden;
    public Boolean ønskerFlerbarnsdager;

    public Tidsperiode tidsperiode;
    public String forelder;
    public List<String> vedlegg;

}
