package net.peepocloud.node.updater;
/*
 * Created by Mc_Ruben on 10.11.2018
 */

import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UpdateCheckResponse {
    private int versionsBehind;
    private String newestVersion;
    private boolean upToDate;
}
