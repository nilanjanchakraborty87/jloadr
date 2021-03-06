package de.adito.jloadr.repository;

import de.adito.jloadrLib.api.IResourceId;
import de.adito.jloadrLib.common.XMLUtil;
import de.adito.jloadrLib.repository.ResourceId;
import org.w3c.dom.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author j.boesl, 22.12.16
 */
public class JLoaderConfig
{
  public static final IResourceId CONFIG_ID = new ResourceId("jloadrConfig.xml");

  public static final String TAG_JAVA = "java";
  public static final String TAG_VM_PARAMETER = "vmParameter";
  public static final String TAG_CLASSPATH = "classpath";
  public static final String TAG_MAIN = "main";
  public static final String TAG_ARGUMENT = "argument";

  private String javaCmd;
  private List<String> vmParameters;
  private List<String> classpath;
  private String mainCls;
  private List<String> arguments;


  public void load(InputStream pInputStream)
  {
    Document document = XMLUtil.loadDocument(pInputStream);
    Element root = document.getDocumentElement();

    javaCmd = XMLUtil.getChildText(root, TAG_JAVA);

    vmParameters = XMLUtil.findChildElements(root, TAG_VM_PARAMETER).stream()
        .map(element -> element.getTextContent().trim())
        .collect(Collectors.toList());

    classpath = XMLUtil.findChildElements(root, TAG_CLASSPATH).stream()
        .map(element -> element.getTextContent().trim())
        .collect(Collectors.toList());

    mainCls = XMLUtil.getChildText(root, TAG_MAIN);
    assert mainCls != null;

    arguments = XMLUtil.findChildElements(root, TAG_ARGUMENT).stream()
        .map(element -> element.getTextContent().trim())
        .collect(Collectors.toList());
  }

  public void save(OutputStream pOutputStream)
  {
    XMLUtil.saveDocument(pOutputStream, pDocument -> {
      Element root = pDocument.createElement("jloadr");
      pDocument.appendChild(root);
      _append(pDocument, root, TAG_JAVA, javaCmd);
      _append(pDocument, root, TAG_VM_PARAMETER, vmParameters);
      _append(pDocument, root, TAG_CLASSPATH, classpath);
      _append(pDocument, root, TAG_MAIN, mainCls);
      _append(pDocument, root, TAG_ARGUMENT, arguments);
    });
  }

  public String[] getStartCommands(Path pWorkingDirectory)
  {
    List<String> parameters = new ArrayList<>();
    parameters.add(_getStartJavaCommand(pWorkingDirectory));
    getVmParameters().stream()
        .map(param -> "-D" + param)
        .forEach(parameters::add);

    String cp = getClasspath().stream()
        .map(str -> str.replace('/', File.separatorChar))
        .collect(Collectors.joining(File.pathSeparator));
    if (!cp.isEmpty())
    {
      parameters.add("-cp");
      parameters.add(cp);
    }
    parameters.add(getMainCls());
    getArguments().forEach(parameters::add);

    return parameters.toArray(new String[parameters.size()]);
  }

  private String _getStartJavaCommand(Path pWorkingDirectory)
  {
    String javaCmd = getJavaCmd();
    if (javaCmd == null)
      javaCmd = "java";
    else
    {
      javaCmd = javaCmd.replace('/', File.separatorChar);
      if (pWorkingDirectory != null)
        javaCmd = pWorkingDirectory.resolve(javaCmd).toAbsolutePath().toString();
    }
    return javaCmd;
  }

  public String getJavaCmd()
  {
    return javaCmd;
  }

  public void setJavaCmd(String pJavaCmd)
  {
    javaCmd = pJavaCmd;
  }

  public List<String> getVmParameters()
  {
    return vmParameters;
  }

  public void setVmParameters(List<String> pVmParameters)
  {
    vmParameters = pVmParameters;
  }

  public List<String> getClasspath()
  {
    return classpath;
  }

  public void setClasspath(List<String> pClasspath)
  {
    classpath = pClasspath;
  }

  public String getMainCls()
  {
    return mainCls;
  }

  public void setMainCls(String pMainCls)
  {
    mainCls = pMainCls;
  }

  public List<String> getArguments()
  {
    return arguments;
  }

  public void setArguments(List<String> pArguments)
  {
    arguments = pArguments;
  }

  private void _append(Document pDocument, Element pAppendTo, String pTag, String pValue)
  {
    if (pValue != null)
    {
      Element element = pDocument.createElement(pTag);
      element.setTextContent(pValue);
      pAppendTo.appendChild(element);
    }
  }

  private void _append(Document pDocument, Element pAppendTo, String pTag, List<String> pValues)
  {
    if (pValues != null)
    {
      for (String value : pValues)
      {
        Element element = pDocument.createElement(pTag);
        element.setTextContent(value);
        pAppendTo.appendChild(element);
      }
    }
  }

}
