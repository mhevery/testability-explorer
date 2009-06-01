package com.google.test.metric.eclipse.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.internal.ide.IMarkerImageProvider;

public class TestabilityMarkerImageProvider implements IMarkerImageProvider {

  public TestabilityMarkerImageProvider() {
    // TODO Auto-generated constructor stub
  }

  public String getImagePath(IMarker marker) {
    // TODO Auto-generated method stub
    return "icons/projects.gif";
  }

}
