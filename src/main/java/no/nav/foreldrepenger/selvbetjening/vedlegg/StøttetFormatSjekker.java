package no.nav.foreldrepenger.selvbetjening.vedlegg;

import no.nav.foreldrepenger.selvbetjening.innsending.domain.VedleggFrontend;
import no.nav.foreldrepenger.selvbetjening.mellomlagring.Attachment;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;
import static no.nav.foreldrepenger.selvbetjening.vedlegg.VedleggUtil.mediaType;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

@Service
public class StøttetFormatSjekker implements VedleggSjekker {


    @Override
    public void sjekk(VedleggFrontend... vedlegg) {
        safeStream(vedlegg).forEach(v -> check(v.getContent()));
    }

    @Override
    public void sjekk(Attachment... vedlegg) {
        safeStream(vedlegg).forEach(v -> check(v.getBytes()));
    }

    private static void check(byte[] content) {
        final var supportedTypes = List.of(IMAGE_JPEG, IMAGE_PNG, APPLICATION_PDF);
        var detectedType = mediaType(content);
        if (!supportedTypes.contains(detectedType)) {
            throw new AttachmentTypeUnsupportedException(detectedType);
        };
    }
}
