package de.adito.jloadr.repository;

import de.adito.jloadrLib.api.*;
import de.adito.jloadrLib.common.JLoadrUtil;
import de.adito.jloadrLib.repository.ResourceId;

import java.io.*;
import java.net.*;
import java.util.Objects;

/**
 * @author j.boesl, 05.09.16
 */
public class URLResource implements IResource
{
  private URL url;
  private URLConnection urlConnection;

  public URLResource(URL pUrl)
  {
    url = pUrl;
  }

  public void checkAvailable() throws IOException
  {
    _getUrlConnection().getInputStream();
  }

  @Override
  public IResourceId getId()
  {
    String host = url.getHost();
    int port = url.getPort();
    String path = url.getPath();
    return new ResourceId(host + (port == -1 ? "" : "." + port) + (path.startsWith("/") ? "" : "/") + path);
  }

  @Override
  public long getSize() throws IOException
  {
    return _getUrlConnection().getContentLengthLong();
  }

  @Override
  public long getLastModified() throws IOException
  {
    return _getUrlConnection().getLastModified();
  }

  @Override
  public String getHash()
  {
    return null;
    // TODO: Der Hash der Datei vom Server wird aus der "indexDatei" genommen, welche vom Server abegrufen werden können soll
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return _getUrlConnection().getInputStream();
  }

  private synchronized URLConnection _getUrlConnection() throws IOException
  {
    if (urlConnection == null)
    {
      urlConnection = url.openConnection();
      urlConnection.setUseCaches(true);
      urlConnection.connect();
    }
    return urlConnection;
  }

  @Override
  public boolean equals(Object pO)
  {
    if (this == pO)
      return true;
    if (pO == null || getClass() != pO.getClass())
      return false;
    URLResource that = (URLResource) pO;
    return Objects.equals(url, that.url);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(url);
  }

  @Override
  public String toString()
  {
    return JLoadrUtil.toSimpleInfo(this, url.toExternalForm());
  }

}
