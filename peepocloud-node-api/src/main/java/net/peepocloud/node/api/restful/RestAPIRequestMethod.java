package net.peepocloud.node.api.restful;
/*
 * Created by Mc_Ruben on 07.01.2019
 */

public enum RestAPIRequestMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    COPY,
    HEAD,
    OPTIONS,
    LINK,
    UNLINK,
    PURGE,
    LOCK,
    UNLOCK,
    PROPFIND,
    VIEW;

    public static RestAPIRequestMethod getByName(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

}
