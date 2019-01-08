package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

@AllArgsConstructor
public class DefaultRestAPIResponseBody implements RestAPIResponseBody {

    private OutputStream outputStream;

    @Override
    public void write(int b) {
        try {
            this.outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            this.outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] bytes, int off, int len) {
        try {
            this.outputStream.write(bytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
