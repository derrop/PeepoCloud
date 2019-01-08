package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

public interface RestAPIResponseBody {

    void write(int b);

    void write(byte[] bytes);

    void write(byte[] bytes, int off, int len);

}
