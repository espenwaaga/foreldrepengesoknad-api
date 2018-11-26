package no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.selvbetjening.tjeneste.AbstractRestConnection;
import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.domain.AktørId;
import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.dto.PersonDto;
import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.dto.SøkerinfoDto;

@Component
public class OppslagConnection extends AbstractRestConnection {

    private final OppslagConfig config;

    public OppslagConnection(RestOperations operations, OppslagConfig config) {
        super(operations);
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String ping() {
        return ping(pingURI());
    }

    public PersonDto hentPerson() {
        return getForObject(config.getPersonURI(), PersonDto.class);
    }

    public SøkerinfoDto hentSøkerInfo() {
        return getForObject(config.getSøkerinfoURI(), SøkerinfoDto.class);
    }

    public AktørId HentAktørId(String fnr) {
        return getForObject(config.getAktørIdURI(fnr), AktørId.class, true);
    }

    public URI pingURI() {
        return config.getPingURI();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [config=" + config + "]";
    }
}
