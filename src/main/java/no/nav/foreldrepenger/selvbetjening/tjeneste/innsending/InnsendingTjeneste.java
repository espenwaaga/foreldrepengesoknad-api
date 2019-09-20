package no.nav.foreldrepenger.selvbetjening.tjeneste.innsending;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.Ettersending;
import no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.Kvittering;
import no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.Søknad;
import no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.Vedlegg;
import no.nav.foreldrepenger.selvbetjening.tjeneste.innsending.domain.VedleggSjekker;
import no.nav.foreldrepenger.selvbetjening.tjeneste.mellomlagring.MellomlagringTjeneste;

@Service
@ConditionalOnProperty(name = "stub.mottak", havingValue = "false", matchIfMissing = true)
public class InnsendingTjeneste implements Innsending {

    private static final Logger LOG = getLogger(InnsendingTjeneste.class);

    private final InnsendingConnection connection;
    private final MellomlagringTjeneste mellomlagring;
    private final VedleggSjekker vedleggSjekker;

    public InnsendingTjeneste(InnsendingConnection connection, MellomlagringTjeneste mellomlagring,
            VedleggSjekker vedleggSjekker) {
        this.connection = connection;
        this.mellomlagring = mellomlagring;
        this.vedleggSjekker = vedleggSjekker;
    }

    @Override
    public Kvittering sendInn(Søknad søknad) {
        LOG.info("Sender inn søknad av type {}", søknad.getType());
        hentOgSjekk(søknad.getVedlegg());
        Kvittering kvittering = connection.sendInn(søknad);
        slettMellomlagringOgSøknad(søknad);
        return kvittering;
    }

    @Override
    public Kvittering sendInn(Ettersending ettersending) {
        LOG.info("Sender inn ettersending på sak {}", ettersending.getSaksnummer());
        hentOgSjekk(ettersending.getVedlegg());
        Kvittering kvittering = connection.ettersend(ettersending);
        ettersending.getVedlegg().forEach(v -> mellomlagring.slettVedlegg(v));
        return kvittering;
    }

    @Override
    public Kvittering endre(Søknad søknad) {
        LOG.info("Sender inn endringssøknad av type {}", søknad.getType());
        hentOgSjekk(søknad.getVedlegg());
        Kvittering kvittering = connection.endre(søknad);
        slettMellomlagringOgSøknad(søknad);
        return kvittering;
    }

    private void hentOgSjekk(List<Vedlegg> vedlegg) {
        vedlegg.forEach(this::hentVedlegg);
        vedleggSjekker.sjekk(vedlegg);
    }

    private void slettMellomlagringOgSøknad(Søknad søknad) {
        LOG.info("Sletter mellomlagret søknad og vedlegg");
        søknad.getVedlegg().forEach(v -> mellomlagring.slettVedlegg(v));
        mellomlagring.slettSøknad();
    }

    private void hentVedlegg(Vedlegg vedlegg) {
        if (vedlegg.getUrl() != null) {
            vedlegg.setContent(mellomlagring.hentVedlegg(vedlegg.getUuid())
                    .map(a -> a.bytes)
                    .orElse(new byte[] {}));
        }
    }

    @Override
    public String ping() {
        return connection.ping();
    }

    @Override
    public URI pingURI() {
        return connection.pingURI();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[connection=" + connection + ", mellomlagring=" + mellomlagring
                + ", vedleggSjekker=" + vedleggSjekker + "]";
    }

}
