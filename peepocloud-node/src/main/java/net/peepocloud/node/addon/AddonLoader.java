package net.peepocloud.node.addon;
/*
 * Created by Mc_Ruben on 05.11.2018
 */

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.peepocloud.node.PeepoCloudNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class AddonLoader {

    @Getter
    private URLClassLoader classLoader;

    public AddonLoader(Path file, ClassLoader parent) throws MalformedURLException {
        if (parent != null) {
            this.classLoader = new URLClassLoader(new URL[]{file.toUri().toURL()}, parent);
        } else {
            this.classLoader = new URLClassLoader(new URL[]{file.toUri().toURL()});
        }
    }

    public AddonLoader(Path file) throws MalformedURLException {
        this(file, null);
    }

    public Addon loadAddon(AddonConfig config) {
        Addon t = null;
        try {
            Class<?> class_ = this.classLoader.loadClass(config.getMain());
            if (class_ != null) {
                Preconditions.checkArgument(Addon.class.isAssignableFrom(class_), "main class of addon " + config.getName() + " was not an instance of " + Addon.class.getName());
                t = (Addon) class_.getConstructor().newInstance();
                t.setAddonConfig(config);
                t.setAddonLoader(this);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.err.println(PeepoCloudNode.getInstance().getLanguagesManager().getMessage("addons.loadingMainClassError")
                    .replace("%main%", config.getMain()).replace("%addon%", config.getName()));
            try {
                this.classLoader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return t;
    }

}
