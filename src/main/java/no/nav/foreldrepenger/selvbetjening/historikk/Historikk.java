package no.nav.foreldrepenger.selvbetjening.historikk;

import java.util.List;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.selvbetjening.http.Pingable;
import no.nav.foreldrepenger.selvbetjening.http.RetryAware;

public interface Historikk extends Pingable, RetryAware {

    List<HistorikkInnslag> historikk();

    List<HistorikkInnslag> historikkFor(Fødselsnummer fnr);

    List<String> manglendeVedlegg(Saksnummer saksnr);

    List<String> manglendeVedleggFor(Fødselsnummer fnr, Saksnummer saksnr);

}
