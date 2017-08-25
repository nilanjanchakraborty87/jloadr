package de.adito.jloadr.api;

import de.adito.jloadrLib.api.IResourcePack;

import java.net.URL;

/**
 * @author j.boesl, 25.01.17
 */
public interface IResourcePackFactory
{

  IResourcePack load(URL pUrl);

}
