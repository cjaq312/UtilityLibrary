package com.jagan.utilitylibrary;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XpathUtil {

	public static String xPathEvalSingle(String content, String expression) throws XPathExpressionException {
		List<String> result = xPathEvalList(content, expression);
		if (result.size() > 0)
			return result.get(0);
		else
			return null;
	}

	public static List<String> xPathEvalList(String content, String expression) throws XPathExpressionException {
		List<String> retValues = new ArrayList<String>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(content));
		NodeList ox = (NodeList)xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
		for (int i = 0; i < ox.getLength(); i++) {
			retValues.add(ox.item(i).getNodeValue());
		}

		return retValues;
	}

	public static Node xPathEvalSingleNode(String content, String expression) throws XPathExpressionException {
		List<Node> lst = xPathEvalListNodes(content, expression);
		if (lst.size() > 0) return lst.get(0);
		return null;
	}

	public static List<Node> xPathEvalListNodes(String content, String expression) throws XPathExpressionException {
		List<Node> retValues = new ArrayList<Node>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(content));
		NodeList ox = (NodeList)xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
		for (int i = 0; i < ox.getLength(); i++) {
			retValues.add(ox.item(i));
		}

		return retValues;
	}

	public static String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		if (node == null) return "";
		StringWriter sw = new StringWriter();
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));
		return sw.toString();
	}

}
