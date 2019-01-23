package net.peepocloud.node.api.libs;
/*
 * Created by Mc_Ruben on 22.01.2019
 */

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultMavenRepositories {

    public static final String CENTRAL = "https://repo.maven.apache.org/maven2/";
    public static final String SONATYPE = "https://oss.sonatype.org/content/repositories/releases/";
    public static final String JCENTER = "http://jcenter.bintray.com/";

    private static final Map<String, String> REPOSITORIES;

    static {
        Map<String, String> repos = new HashMap<>();
        for (Field field : DefaultMavenRepositories.class.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                try {
                    repos.put(field.getName(), String.valueOf(field.get(null)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        REPOSITORIES = Collections.unmodifiableMap(repos);
    }

    public static Map<String, String> getRepositories() {
        return REPOSITORIES;
    }

    public static String parse(String repo) {
        if (repo.startsWith("default:")) {
            return REPOSITORIES.getOrDefault(repo.substring(8).toUpperCase(), repo);
        }
        return repo;
    }

}
