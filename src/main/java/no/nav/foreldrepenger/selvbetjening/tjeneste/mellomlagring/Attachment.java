package no.nav.foreldrepenger.selvbetjening.tjeneste.mellomlagring;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;

import no.nav.foreldrepenger.selvbetjening.error.AttachmentTooLargeException;
import no.nav.foreldrepenger.selvbetjening.util.StringUtil;

public class Attachment {

    public static final DataSize MAX_VEDLEGG_SIZE = DataSize.of(8, DataUnit.MEGABYTES);

    public final String filename;
    public final byte[] bytes;
    public final MediaType contentType;
    public final long size;
    public final String uuid;

    private Attachment(String filename, byte[] bytes, MediaType contentType) {
        this.filename = filename;
        this.bytes = bytes;
        this.contentType = contentType;
        this.size = bytes.length;
        this.uuid = UUID.randomUUID().toString();
        if (size > MAX_VEDLEGG_SIZE.toBytes()) {
            throw new AttachmentTooLargeException(size, MAX_VEDLEGG_SIZE);
        }
    }

    public static Attachment of(MultipartFile file) {
        return new Attachment(file.getOriginalFilename(), getBytes(file), MediaType.valueOf(file.getContentType()));
    }

    static Attachment of(String fileName, byte[] bytes, MediaType mediaType) {
        return new Attachment(fileName, bytes, mediaType);
    }

    private static byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Attachment fromJson(String json) {
        return new Gson().fromJson(json, Attachment.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public URI uri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{uuid}")
                .buildAndExpand(this.uuid).toUri();
    }

    public ResponseEntity<byte[]> asOKHTTPEntity() {
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(size)
                .body(bytes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [filename=" + filename + ", bytes=" + StringUtil.limit(bytes)
                + ", contentType=" + contentType
                + ", size=" + size + ", uuid=" + uuid + "]";
    }
}
