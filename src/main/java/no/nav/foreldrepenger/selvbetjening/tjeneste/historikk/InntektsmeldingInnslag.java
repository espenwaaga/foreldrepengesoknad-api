package no.nav.foreldrepenger.selvbetjening.tjeneste.historikk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.selvbetjening.tjeneste.oppslag.domain.Fødselsnummer;

public class InntektsmeldingInnslag extends HistorikkInnslag {

    private final Arbeidsgiver arbeidsgiver;

    @JsonCreator
    public InntektsmeldingInnslag(@JsonProperty("fnr") Fødselsnummer fnr,
            @JsonProperty("arbeidsgiver") Arbeidsgiver arbeidsgiver) {
        super(fnr);
        this.arbeidsgiver = arbeidsgiver;
    }

    public Arbeidsgiver getArbeidsgiver() {
        return arbeidsgiver;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[fnr=" + getFnr() + ", aktørId=" + getAktørId() + ", journalpostId="
                + getJournalpostId() + ", saksnr=" + getSaksnr() + ", opprettet=" + opprettet + ", arbeidsgiver="
                + arbeidsgiver + "]";
    }
}
