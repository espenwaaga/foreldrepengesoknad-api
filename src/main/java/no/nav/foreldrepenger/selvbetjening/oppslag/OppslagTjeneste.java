package no.nav.foreldrepenger.selvbetjening.oppslag;

import static no.nav.foreldrepenger.selvbetjening.util.MDCUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.selvbetjening.util.StringUtil.maskFnr;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.selvbetjening.innsyn.InnsynConnection;
import no.nav.foreldrepenger.selvbetjening.oppslag.domain.AktørId;
import no.nav.foreldrepenger.selvbetjening.oppslag.domain.Person;
import no.nav.foreldrepenger.selvbetjening.oppslag.domain.Søkerinfo;

@Service
@ConditionalOnProperty(name = "stub.oppslag", havingValue = "false", matchIfMissing = true)
public class OppslagTjeneste implements Oppslag {

    private static final Logger LOG = LoggerFactory.getLogger(OppslagTjeneste.class);
    private final OppslagConnection oppslag;
    private final InnsynConnection innsyn;

    @Inject
    public OppslagTjeneste(OppslagConnection oppslag, InnsynConnection innsyn) {
        this.oppslag = oppslag;
        this.innsyn = innsyn;
    }

    @Override
    public Person hentPerson() {
        return sammenlign(pdlPerson(), tpsPerson());
    }

    private Person sammenlign(Person pdl, Person tps) {
        if (!tps.equals(pdl)) {
            LOG.warn("TPS-person {} og PDL-person {} er ulike", tps, pdl);
        } else {
            LOG.info("TPS-person og PDL-person er like");
        }
        return tps;
    }

    private Person tpsPerson() {
        return new Person(oppslag.hentPerson());
    }

    private Person pdlPerson() {
        try {
            LOG.info("Henter PDL-person");
            var pdl = oppslag.hentPDLPerson();
            LOG.info("PDL-person hentet {}", pdl);
            var p = new Person(pdl);
            LOG.info("PDL-person {} mappet til {}", pdl, p);
            return p;
        } catch (Exception e) {
            LOG.warn("Feil ved oppslag av PDL-person", e);
            return null;
        }
    }

    @Override
    public Søkerinfo hentSøkerinfo() {
        return tpsSøkerinfo();
    }

    private Søkerinfo tpsSøkerinfo() {
        LOG.info("Henter TPS-søkerinfo");
        var info = new Søkerinfo(hentPerson(), innsyn.hentArbeidsForhold());
        LOG.info("Hentet TPS-søkerinfo for {} med {} arbeidsforhold OK", maskFnr(info.getSøker().fnr), info.getArbeidsforhold());
        LOG.info(CONFIDENTIAL, "Hentet TPS-søkerinfo {}", info);
        return info;
    }

    @Override
    public AktørId hentAktørId(String fnr) {
        return oppslag.HentAktørId(fnr);
    }

    @Override
    public String ping() {
        return oppslag.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + "]";
    }

}
