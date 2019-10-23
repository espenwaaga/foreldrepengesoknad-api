package no.nav.foreldrepenger.selvbetjening.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.selvbetjening.tjeneste.mellomlagring.GCPCloudStorage;
import no.nav.foreldrepenger.selvbetjening.tjeneste.mellomlagring.Storage;
import no.nav.foreldrepenger.selvbetjening.util.ConditionalOnGCP;

@Configuration
@ConditionalOnGCP
public class S3GCPStorageConfiguration {

    @Bean
    public Storage gcpCloudStorage(@Value("${storage.søknad:foreldrepengesoknad}") String søknadBucket,
            @Value("${storage.mellomlagring:mellomlagring}") String mellomlagringBucket) {
        return new GCPCloudStorage(søknadBucket, mellomlagringBucket);
    }

}
