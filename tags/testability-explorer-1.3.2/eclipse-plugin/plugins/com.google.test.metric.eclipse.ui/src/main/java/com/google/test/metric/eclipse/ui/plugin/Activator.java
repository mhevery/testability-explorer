/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric.eclipse.ui.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
  private Map<String, Image> images = new HashMap<String, Image>();
  
  // The plug-in ID
  public static final String PLUGIN_ID = "com.google.test.metric.eclipse.ui";

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
   * )
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
   * )
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  public Image getImage(String path) throws ImageNotFoundException {
    Image image = images.get(path);
    if (image == null) {
      String pluginLocation = Activator.getDefault().getBundle().getLocation();
      if (pluginLocation.startsWith("reference:")) {
        pluginLocation = pluginLocation.substring(10);
      }
      URL url;
      try {
        url = new URL(pluginLocation + path);
      } catch (MalformedURLException e) {
        throw new ImageNotFoundException("Image : " + path + " not found");
      }
  
      ImageDescriptor projectImageDescriptor = ImageDescriptor.createFromURL(url);
      image = projectImageDescriptor.createImage();
      images.put(path, image);
    }
    return image;
  }
}
