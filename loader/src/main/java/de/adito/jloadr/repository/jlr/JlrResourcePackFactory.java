package de.adito.jloadr.repository.jlr;

import de.adito.jloadr.api.IResourcePackFactory;
import de.adito.jloadr.common.UrlUtil;
import de.adito.jloadrLib.api.IResourcePack;

import java.io.IOException;
import java.net.URL;

/**
 * @author j.boesl, 25.01.17
 */
public class JlrResourcePackFactory implements IResourcePackFactory
{
  public static final String CONFIG_FILE_SUFIX = ".jlr.xml";

  @Override
  public IResourcePack load(URL pUrl)
  {
    String path = pUrl.getPath();
    if (path.endsWith(CONFIG_FILE_SUFIX))
    {
      URL resourcesUrl = UrlUtil.getAtHost(pUrl, path.substring(0, path.lastIndexOf(CONFIG_FILE_SUFIX)) + "/");
      return new JlrResourcePack(pUrl, resourcesUrl);
    }

    if (path.endsWith("/"))
      path = path.substring(0, path.length() - 1);
    path += CONFIG_FILE_SUFIX;
    try
    {
      URL packUrl = UrlUtil.getAtHost(pUrl, path);
      packUrl.openStream().close(); // check existence
      return new JlrResourcePack(packUrl, pUrl);
    }
    catch (IOException pE)
    {
      // ignore
    }
    return null;
  }

}
