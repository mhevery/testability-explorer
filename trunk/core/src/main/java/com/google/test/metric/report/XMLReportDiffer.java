package com.google.test.metric.report;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;

import static com.google.test.metric.report.XMLReport.*;

/**
 * Calculate the differences between classes and methods in two XML
 * testability reports.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class XMLReportDiffer {
  Logger logger = Logger.getLogger(XMLReportDiffer.class.getCanonicalName());

  public Diff diff(Document oldDoc, Document newDoc) {
    List<Diff.ClassDiff> result = new LinkedList<Diff.ClassDiff>();

    NodeList oldClasses = oldDoc.getElementsByTagName(CLASS_NODE);
    NodeList newClasses = newDoc.getElementsByTagName(CLASS_NODE);

    Map<String, Node> oldClassMap = buildChildMap(oldClasses, CLASS_NAME_ATTRIBUTE);
    Map<String, Node> newClassMap = buildChildMap(newClasses, CLASS_NAME_ATTRIBUTE);

    Set<String> classNames = new HashSet<String>();
    classNames.addAll(oldClassMap.keySet());
    classNames.addAll(newClassMap.keySet());

    for (String className : classNames) {
      Integer oldCost = getNumericalAttributeOfNode(oldClassMap, className, CLASS_COST_ATTRIBUTE);
      Integer newCost = getNumericalAttributeOfNode(newClassMap, className, CLASS_COST_ATTRIBUTE);

      List<Diff.MethodDiff> methodDiffs = diffMethods(oldClassMap.get(className), newClassMap.get(className));

      if (!methodDiffs.isEmpty() || different(oldCost, newCost)) {
        result.add(new Diff.ClassDiff(className,  oldCost, newCost, methodDiffs));
      }
    }

    return new Diff(result);
  }

  private boolean different(Integer oldCost, Integer newCost) {
    return (oldCost == null && newCost != null) || (oldCost != null && (newCost == null || !oldCost.equals(newCost)));
  }

  @SuppressWarnings("unchecked")
  private List<Diff.MethodDiff> diffMethods(Node oldClassNode, Node newClassNode) {
    List<Diff.MethodDiff> result = new LinkedList<Diff.MethodDiff>();

    Set<String> methodNames = new HashSet<String>();
    Map<String, Node> oldMethodMap = Collections.EMPTY_MAP;
    Map<String, Node> newMethodMap = Collections.EMPTY_MAP;

    if (oldClassNode != null) {
      oldMethodMap = buildChildMap(oldClassNode.getChildNodes(), METHOD_NAME_ATTRIBUTE);
      methodNames.addAll(oldMethodMap.keySet());
    }
    if (newClassNode != null) {
      newMethodMap = buildChildMap(newClassNode.getChildNodes(), METHOD_NAME_ATTRIBUTE);
      methodNames.addAll(newMethodMap.keySet());
    }

    for (String methodName : methodNames) {
      Integer oldCost = getNumericalAttributeOfNode(oldMethodMap, methodName, METHOD_OVERALL_COST_ATTRIBUTE);
      Integer newCost = getNumericalAttributeOfNode(newMethodMap, methodName, METHOD_OVERALL_COST_ATTRIBUTE);
      if (different(oldCost, newCost)) {
         result.add(new Diff.MethodDiff(methodName, oldCost, newCost));
      }
    }
    return result;
  }

  private Integer getNumericalAttributeOfNode(Map<String, Node> newClassMap, String className, String attribute) {
    Integer newCost = null;
    if (newClassMap.containsKey(className)) {
      try {
        String stringValue = extractAttribute(newClassMap.get(className), attribute);
        if (stringValue != null) {
          newCost = Integer.valueOf(stringValue);
        }
      } catch (NumberFormatException e) {
        logger.log(Level.WARNING,
            String.format("Invalid cost attribute for class %s", className), e);
      }
    }
    return newCost;
  }

  private Map<String, Node> buildChildMap(NodeList nodeList, String keyAttributeName) {
    Map<String, Node> childMap = new HashMap<String, Node>();

    for (int count = 0; count < nodeList.getLength(); count++) {
      Node node = nodeList.item(count);
      String keyValue = extractAttribute(node, keyAttributeName);
      childMap.put(keyValue, node);
    }
    return childMap;
  }

  private String extractAttribute(Node node, String classNameAttribute) {
    if (node == null || node.getAttributes() == null || node.getAttributes().getNamedItem(classNameAttribute) == null) {
      return null;
    }
    return node.getAttributes().getNamedItem(classNameAttribute).getNodeValue();
  }
}
