package com.aionemu.commons.scripting;

import com.aionemu.commons.utils.ClassUtils;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScriptingXmlApplicationContext for spring.
 *
 * Author: SoulKeeper
 */
public class ScriptingSpringApplicationContext extends FileSystemXmlApplicationContext{

    /**
     * Returns classLoader
     */
    private ScriptClassLoader classLoader;

    /**
     * Creates new instance of ApplicationContext
     * @param xmlPath path of xml beans definitions for spring
     * @param cl classloader to use
     * @param parent Parent ApplicationContext
     */
    public ScriptingSpringApplicationContext(File xmlPath, ScriptClassLoader cl, ApplicationContext parent){
        super(new String[]{xmlPath.getPath()}, false, parent);
        this.classLoader = cl;
        refresh();

    }

    /**
     * Returns classloader for this ApplicationContext
     * @return classLoader for this application context
     */
    @Override
    public ScriptClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Closes application context.
     * Clears classloader after closing
     */
    @Override
    public void close() {
        super.close();
        // Clear spring cache. In other case here will be a memory leak
        CachedIntrospectionResults.clearClassLoader(this.classLoader);
        this.classLoader = null;
    }

    /**
     * Responsible for loading bean definitions with component-scan, very limited implementation.
     * @param locationPattern location pattern for component-scan
     * @return array of resources
     * @throws IOException if something went wrong
     */
    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        List<Resource> result = new ArrayList<Resource>();
        if(locationPattern.startsWith("classpath*:") && locationPattern.endsWith("/**/*.class")){
            String packagePattern = locationPattern.replace("classpath*:", "").replace("/**/*.class", "");
            packagePattern = packagePattern.replace('/', '.');

            for(String s : getClassLoader().getCompiledClasses()){
                if(ClassUtils.isPackageOrSubPackageMember(s, packagePattern)){
                   result.add(new ClassPathResource(s + ".class", getClassLoader()));
                }
            }
        }

        Collections.addAll(result, super.getResources(locationPattern));
        return result.toArray(new Resource[result.size()]);
    }
}
