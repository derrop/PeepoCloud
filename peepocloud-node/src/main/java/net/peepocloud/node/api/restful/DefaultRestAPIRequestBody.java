package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class DefaultRestAPIRequestBody implements RestAPIRequestBody {

    private final InputStream inputStream;
    private byte[] asBytes;

    @Override
    public String asString(Charset charset) {
        if (asBytes() == null)
            return null;
        return new String(asBytes, charset);
    }

    @Override
    public String asString() {
        return asString(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] asBytes() {
        if (asBytes == null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                IOUtils.copy(this.inputStream, byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.asBytes = byteArrayOutputStream.toByteArray();
        }
        if (this.asBytes.length == 0)
            return null;
        return this.asBytes;
    }

    @Override
    public int read() {
        try {
            return inputStream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int read(byte[] bytes) {
        try {
            return inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        try {
            return inputStream.read(bytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
