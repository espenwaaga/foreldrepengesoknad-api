package no.nav.foreldrepenger.selvbetjening.tjeneste.innsyn;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "innsyn")
@Configuration
public class InnsynConfig {

    private static final String BASE_PATH = "/api";
    private static final String PING = "mottak/ping";
    static final String FPSAK_SAKER = "innsyn/saker";
    static final String SAK_SAKER = "sak";
    static final String SAKSNUMMER = "saksnummer";
    static final String UTTAKSPLAN = "innsyn/uttaksplan";

    boolean enabled;
    URI mottakURI;
    URI oppslagURI;

    public InnsynConfig(@Value("${FPSOKNAD_MOTTAK_API_URL}") URI mottakURI,
            @Value("${FPSOKNAD_OPPSLAG_API_URL}") URI oppslagURI) {
        this.mottakURI = uri(mottakURI, BASE_PATH);
        this.oppslagURI = uri(oppslagURI, BASE_PATH);
    }

    public URI getOppslagURI() {
        return oppslagURI;
    }

    public void setOppslagURI(URI oppslagURI) {
        this.oppslagURI = oppslagURI;
    }

    private URI getMottakURI() {
        return mottakURI;
    }

    public void setMottakURI(URI mottakURI) {
        this.mottakURI = mottakURI;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    URI getPingURI() {
        return uri(getMottakURI(), PING);
    }

    URI getFpsakURI() {
        return uri(getMottakURI(), FPSAK_SAKER);
    }

    URI getSakURI() {
        return uri(getOppslagURI(), SAK_SAKER);
    }

    URI getUttakURI(String saksnummer) {
        return uri(getMottakURI(), UTTAKSPLAN, queryParams(SAKSNUMMER, saksnummer));
    }

    protected static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    protected static URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path, queryParams)
                .build()
                .toUri();
    }

    private static UriComponentsBuilder builder(URI base, String path, HttpHeaders queryParams) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path)
                .queryParams(queryParams);

    }

    protected static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

}
