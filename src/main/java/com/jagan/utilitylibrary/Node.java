package com.jagan.utilitylibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	private Map<String, String> attrMap = new HashMap<String, String>();
	private List<Node> children = new ArrayList<Node>();
	private String key;
	private String value;
	private String encoding = "UTF-8";
	public Node prev;

	public Node (String name) {
		this.key = name;
		if (this.key != null && this.key.length() > 1) {
			this.key = this.key.substring(0, 1).toUpperCase() + this.key.substring(1);
		}
	}

	public Node (String key, String value) {
		this.key = key;
		this.value = value;
		if (this.key != null && this.key.length() > 1) {
			this.key = this.key.substring(0, 1).toUpperCase() + this.key.substring(1);
		}
	}

	public Node () {
	}

	public void setKey(String key) {
		this.key = key;
		if (this.key != null && this.key.length() > 1) {
			this.key = this.key.substring(0, 1).toUpperCase() + this.key.substring(1);
		}
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "Node [key=" + key + "] " + value;
	}

	public Node addNode(Node node) {
		children.add(node);
		return node;
	}

	public int childCount() {
		return children.size();
	}

	public Node getNode(int x) {
		if (x > children.size()) return null;
		return children.get(x);
	}

	public Node getNode(String key) {
		Node n = this.get(key);
		if (n == null) {

		}
		return null;
	}

	public void addAttributes(String string) {
		for (;;) {
			int x = string.indexOf("=");
			if (x < 0) return;

			String key = string.substring(0, x);
			x = string.indexOf("=\"");
			x += 2;
			int y = string.substring(x).indexOf("\"");
			y += x;
			try {
				String value = string.substring(x, y);
				attrMap.put(key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (y < string.length()) {
				string = string.substring(y + 1);
			} else {
				return;
			}
		}
	}

	public void setValue(String string) {
		this.value = string.trim();
	}

	/**
	 * Returns the node associated with the specified key. The method also performs a recursive search on all children.
	 * 
	 * @param key
	 * @return
	 */
	public Node get(String key) {
		return get(this, key);
	}

	/**
	 * Returns the node associated with the specified key. The method also performs a recursive search on all children of the specified node.
	 * 
	 * @param n
	 * @param key
	 * @return
	 */
	public Node get(Node n, String key) {
		if (n.getKey().equals(key)) {
			return n;
		} else {
			for (Node m : n.getChildren()) {
				Node o = get(m, key);
				if (o != null) {
					return o;
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getValue() {
		if (value == null) return null;
		return StringUtils.removeHtmlEncodings(value);
	}

	/**
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return getValue(this, key);
	}

	/**
	 * @param n
	 * @param key
	 * @return
	 */
	public String getValue(Node n, String key) {
		Node m = get(n, key);
		return m != null ? m.getValue() : "";
	}

	/**
	 * Returns all child nodes associated with this node.
	 * 
	 * @return
	 */
	public List<Node> getChildren() {
		return this.children;
	}

	public List<Node> getChildren(String string) {
		if (getKey().equals(string)) {
			return this.children;
		}
		if (children == null) return null;
		for (int y = 0; y < children.size(); y++) {
			Node n = children.get(y);
			if (n.getKey().equals(string)) {
				return n.children;
			}
		}
		return null;
	}

	public String getAttribute(String key) {
		return attrMap.get(key);
	}

	public List<String[]> getAttributes() {
		List<String[]> rtn = new ArrayList<String[]>();
		for (String key : attrMap.keySet()) {
			String value = attrMap.get(key);
			rtn.add(new String[] {key, value});
		}
		return rtn;
	}

	public String printXML(boolean header) {
		return printXML(header, 0);
	}

	private String printXML(boolean header, int level) {
		StringBuilder builder = new StringBuilder();
		if (header) builder.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>\n");
		for (int x = 0; x < level; x++) {
			builder.append("  ");
		}
		int count = 0;
		builder.append("<").append(getKey());
		for (String[] attribute : getAttributes()) {
			if (count++ > 0) builder.append(",");
			builder.append(" ").append(attribute[0]).append("=\"").append(attribute[1]).append("\"");
		}
		if ((value == null || value.equals("")) && children.size() < 1) {
			builder.append("/>\n");
			return builder.toString();
		}
		builder.append(">");
		if (value != null) {
			builder.append(value);
		} else {
			builder.append("\n");
			for (Node n : children) {
				builder.append(n.printXML(false, level + 1));
			}
			for (int x = 0; x < level; x++) {
				builder.append("  ");
			}
		}
		builder.append("</").append(getKey()).append(">\n");
		return builder.toString();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
