package no.nav.foreldrepenger.selvbetjening.oppslag.tjeneste;

import no.nav.foreldrepenger.selvbetjening.oppslag.json.AnnenForelder;
import no.nav.foreldrepenger.selvbetjening.oppslag.json.Arbeidsforhold;
import no.nav.foreldrepenger.selvbetjening.oppslag.json.Bankkonto;
import no.nav.foreldrepenger.selvbetjening.oppslag.json.Barn;
import no.nav.foreldrepenger.selvbetjening.oppslag.tjeneste.json.Behandling;
import no.nav.foreldrepenger.selvbetjening.oppslag.tjeneste.json.PersonDto;
import no.nav.foreldrepenger.selvbetjening.oppslag.tjeneste.json.Sak;
import no.nav.foreldrepenger.selvbetjening.oppslag.tjeneste.json.SøkerinfoDto;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.neovisionaries.i18n.CountryCode.NO;
import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

@Service
@ConditionalOnProperty(name = "stub.oppslag", havingValue = "true")
public class OppslagstjenesteStub implements Oppslag {

    private static final Logger LOG = getLogger(OppslagstjenesteStub.class);

    @Override
    public PersonDto hentPerson() {
        LOG.info("Stubber oppslag...");
        return person();
    }

    @Override
    public SøkerinfoDto hentSøkerinfo() {
        SøkerinfoDto dto = new SøkerinfoDto();
        dto.person = person();
        dto.arbeidsforhold = new ArrayList<>();
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.arbeidsgiverId = "123456789";
        arbeidsforhold.arbeidsgiverIdType = "orgnr";
        arbeidsforhold.fom = now().minusYears(2);
        arbeidsforhold.arbeidsgiverNavn = "navn";
        arbeidsforhold.stillingsprosent = 100d;
        dto.arbeidsforhold.add(arbeidsforhold);
        return dto;
    }

    public static PersonDto person() {
        PersonDto dto = new PersonDto();
        dto.fnr = "25987148243";
        dto.aktorId = "0123456789999";
        dto.fornavn = "SIV";
        dto.etternavn = "STUBSVEEN";
        dto.fødselsdato = now().minusYears(21);
        dto.kjønn = "K";
        dto.landKode = NO;
        dto.bankkonto = new Bankkonto();
        dto.bankkonto.kontonummer = "1234567890";
        dto.bankkonto.banknavn = "Stub NOR";

        AnnenForelder annenForelder = new AnnenForelder("01017098765", "Steve", "Stubsveen", "Nichols",
                now().minusYears(45));

        Barn barn = new Barn("01011812345", "Mo", null, "Stubsveen", "M", now().minusYears(1), annenForelder);
        dto.barn = singletonList(barn);

        return dto;
    }

    @Override
    public List<Sak> hentSaker() {
        Behandling behandling = new Behandling("abc", "UTRED", "FP", "FORP_FODS", null, "4833", "NAV Torrevieja");
        Sak sak = new Sak("42", "LOP", "FORP_FODS", "1", "2", singletonList("3"), singletonList(behandling));
        return singletonList(sak);
    }

    @Override
    public String hentSøknad(String behandlingId) {
        return "Hello world";
    }
}
