package net.peepocloud.lib.server;
/*
 * Created by Mc_Ruben on 14.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Template {
    private String name;
    private String storage;
}
