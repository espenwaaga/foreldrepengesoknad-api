package no.nav.foreldrepenger.selvbetjening.innsending.json.arbeid;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.nav.foreldrepenger.selvbetjening.innsending.json.Tidsperiode;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class AnnenInntekt {

    public String type;
    public String land;
    public String arbeidsgiverNavn;
    public Tidsperiode tidsperiode;
    public Boolean erNærVennEllerFamilieMedArbeisdgiver;
    public List<String> vedlegg = new ArrayList<>();
}
