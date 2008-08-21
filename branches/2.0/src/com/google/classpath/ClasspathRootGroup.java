package com.google.classpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClasspathRootGroup {
  private final List<ClasspathRoot> roots;

  public ClasspathRootGroup(List<ClasspathRoot> roots) {
    this.roots = roots;
  }

  /* visible for testing */
  int getGroupCount() {
    return roots.size();
  }

  /**
   * Returns the first matching resource in one of the grouped ClasspathRoots
   */
  public InputStream getResourceAsStream(String resourceName) {
    InputStream stream = null;
    for (ClasspathRoot classpathRoot : roots) {
      stream = classpathRoot.getResourceAsStream(resourceName);
      if (stream != null) {
        break;
      }
    }
    return stream;
  }

  public List<String> getClassNamesToEnter(List<String> entryList) {
    List<String> classNames = new ArrayList<String>();
    for (ClasspathRoot classpathRoot : roots) {
      Collection<String> clazzes = classpathRoot
          .getAllContainedClassNames(entryList);
      classNames.addAll(clazzes);
    }
    return classNames;
  }
}
