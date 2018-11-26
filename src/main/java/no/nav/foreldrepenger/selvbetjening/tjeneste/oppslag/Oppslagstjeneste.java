package no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag;

import javax.inject.Inject;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.domain.AktørId;
import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.domain.Person;
import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.domain.Søkerinfo;

@Service
@ConditionalOnProperty(name = "stub.oppslag", havingValue = "false", matchIfMissing = true)
public class Oppslagstjeneste implements Oppslag {

    private final OppslagConnection connection;

    @Inject
    public Oppslagstjeneste(OppslagConnection connection) {
        this.connection = connection;
    }

    @Override
    public Person hentPerson() {
        return new Person(connection.hentPerson());
    }

    @Override
    public Søkerinfo hentSøkerinfo() {
        return new Søkerinfo(connection.hentSøkerInfo());
    }

    @Override
    @Cacheable(cacheNames = "aktoer")
    public AktørId hentAktørId(String fnr) {
        return connection.HentAktørId(fnr);
    }

    public void ping() {
        connection.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + "]";
    }

}
