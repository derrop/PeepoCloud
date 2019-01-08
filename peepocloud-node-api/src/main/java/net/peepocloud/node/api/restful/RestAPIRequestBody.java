package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

import java.nio.charset.Charset;

public interface RestAPIRequestBody {

    String asString(Charset charset);

    String asString();

    byte[] asBytes();

    int read();

    int read(byte[] bytes);

    int read(byte[] bytes, int off, int len);

}
