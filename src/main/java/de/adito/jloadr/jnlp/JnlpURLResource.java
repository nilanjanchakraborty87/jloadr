package de.adito.jloadr.jnlp;

import de.adito.jloadr.api.IResource;
import de.adito.jloadr.common.URLResource;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * IResource-Impl
 */
class JnlpURLResource implements IResource
{
  private JnlpReference jarJnlpReference;
  private URLResource resource;

  public JnlpURLResource(JnlpReference pJarJnlpReference)
  {
    jarJnlpReference = pJarJnlpReference;
  }

  @Nonnull
  @Override
  public String getId()
  {
    return _getResource().getId();
  }

  @Nonnull
  @Override
  public String getHash()
  {
    return _getResource().getHash();
  }

  @Nonnull
  @Override
  public InputStream getInputStream() throws IOException
  {
    return _getResource().getInputStream();
  }

  @Override
  public boolean equals(Object pO)
  {
    if (this == pO)
      return true;
    if (pO == null || getClass() != pO.getClass())
      return false;
    JnlpURLResource that = (JnlpURLResource) pO;
    return Objects.equals(jarJnlpReference, that.jarJnlpReference);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(jarJnlpReference);
  }

  private URLResource _getResource()
  {
    if (resource == null) {
      Element jarElement = jarJnlpReference.getJarElement();
      String href = jarElement.getAttribute("href");
      String version = jarElement.getAttribute("version");

      List<String> variants = new ArrayList<>();
      if (version != null && !version.isEmpty()) {
        int index = href.lastIndexOf(".jar");
        if (index != -1) {
          String name = href.substring(0, index);
          String versionedJar = name + "__V" + version + ".jar";
          variants.add(versionedJar);
          variants.add(versionedJar + ".pack.gz");
        }
      }
      variants.add(href);
      variants.add(href + ".pack.gz");

      for (String variant : variants) {
        try {
          URL url = new URL(jarJnlpReference.getCodebase(), variant);
          URLConnection uc = url.openConnection();
          uc.connect();
          try (InputStream is = uc.getInputStream()) {
            resource = new URLResource(url);
            return resource;
          }
          catch (IOException pE) {
            // ignore
          }
        }
        catch (IOException pE) {
          throw new RuntimeException(pE);
        }
      }
      throw new RuntimeException("resource could not be found: " + jarJnlpReference.getUrl());
    }
    return resource;
  }
}