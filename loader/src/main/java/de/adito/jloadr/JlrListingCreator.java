package de.adito.jloadr;

import de.adito.jloadrLib.api.IStoreResource;
import de.adito.jloadrLib.repository.local.LocalStoreResourcePack;

import java.nio.file.*;

/**
 * @author j.boesl, 21.12.16
 */
public class JlrListingCreator
{

  public static void main(String[] args)
  {
    Path dirPath = Paths.get(args[0]);
    Path configPath = dirPath.getParent().resolve(dirPath.getFileName() + ".jlr.xml");
    LocalStoreResourcePack lsrp = new LocalStoreResourcePack(dirPath, configPath);
    for (IStoreResource resource : lsrp.getResources())
      resource.getHash();
    lsrp.writeConfig();
  }

}
