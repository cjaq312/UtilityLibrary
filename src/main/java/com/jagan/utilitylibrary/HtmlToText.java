package com.jagan.utilitylibrary;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class HtmlToText extends HTMLEditorKit.ParserCallback {
	private final StringBuffer stringBuffer;
	private final Stack<IndexType> indentStack;

	public static class IndexType {
		public String type;
		public int counter; // used for ordered lists

		public IndexType (final String type) {
			this.type = type;
			counter = 0;
		}
	}

	public HtmlToText () {
		stringBuffer = new StringBuffer();
		indentStack = new Stack<IndexType>();
	}

	public static String convert(final String html) {
		final HtmlToText parser = new HtmlToText();
		final Reader in = new StringReader(html);
		try {
			// the HTML to convert
			parser.parse(in);
		} catch (final Exception e) {
			// e.printStackTrace();
			return html;
		} finally {
			try {
				in.close();
			} catch (final IOException ioe) {
				// this should never happen
			}
		}
		return parser.getText();
	}

	public void parse(final Reader in) throws IOException {
		final ParserDelegator delegator = new ParserDelegator();
		// the third parameter is TRUE to ignore charset directive
		delegator.parse(in, this, Boolean.TRUE);
	}

	@Override
	public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
		String tStr = t.toString().toLowerCase();
		try {
			if (tStr.equals("p")) {
				if (stringBuffer.length() > 0 && !stringBuffer.substring(stringBuffer.length() - 1).equals("\n")) {
					newLine();
				}
				newLine();
			} else if (tStr.toLowerCase().equals("table")) {
				indentStack.push(new IndexType("table"));
				newLine();
			} else if (tStr.equals("ol")) {
				indentStack.push(new IndexType("ol"));
				newLine();
			} else if (tStr.equals("ul")) {
				indentStack.push(new IndexType("ul"));
				newLine();
			} else if (tStr.equals("li")) {
				final IndexType parent = indentStack.peek();
				if (parent.type.equals("ol")) {
					final String numberString = "" + (++parent.counter) + ".";
					stringBuffer.append(numberString);
					for (int i = 0; i < (4 - numberString.length()); i++) {
						stringBuffer.append(" ");
					}
				} else {
					stringBuffer.append("*   ");
				}
				indentStack.push(new IndexType("li"));
			} else if (tStr.equals("dl")) {
				newLine();
			} else if (tStr.equals("dt")) {
				newLine();
			} else if (tStr.equals("dd")) {
				indentStack.push(new IndexType("dd"));
				newLine();
			} else if (tStr.equals("tr")) {
				newLine();
			}
		} catch (EmptyStackException e) {
			// doing nothing
			// System.out.println("exception in handleStartTag");
		}
	}

	private void newLine() {
		stringBuffer.append("\n");
		for (int i = 0; i < indentStack.size(); i++) {
			stringBuffer.append("    ");
		}
	}

	private void tab() {
		stringBuffer.append("\t");
	}

	@Override
	public void handleEndTag(final HTML.Tag t, final int pos) {
		String tStr = t.toString().toLowerCase();
		try {
			if (tStr.equals("p")) {
				newLine();
			} else if (tStr.equals("ol")) {
				indentStack.pop();
				;
				newLine();
			} else if (tStr.equals("ul")) {
				indentStack.pop();
				;
				newLine();
			} else if (tStr.equals("li")) {
				indentStack.pop();
				;
				newLine();
			} else if (tStr.equals("dd")) {
				indentStack.pop();
				;
			} else if (tStr.equals("table")) {
				indentStack.pop();
				;
			} else if (tStr.equals("td")) {
				tab();
				;
			}
		} catch (EmptyStackException e) {
			// doing nothing
			// System.out.println("exception in handleStartTag");
		}
	}

	@Override
	public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
		if (t.toString().toLowerCase().equals("br")) {
			newLine();
		}
	}

	@Override
	public void handleText(final char[] text, final int pos) {
		stringBuffer.append(text);
	}

	public String getText() {
		return stringBuffer.toString();
	}

	public static void main(final String args[]) {
		// final String html =
		// "<p> The all new Sealy Posturepedic Mattresses that are designed to eliminate tossing and turning caused by pressure points. Most people do not get the required 8 hours of sleep&hellip;in fact most people only average 6 hours of sleep. If you are only going to get 6 hours of sleep...Get a Better 6!</p> <p> &nbsp;</p> <p> This preferred level plush euro pillowtop sleep set represents an exceptional value from the number one name in Mattresses...Sealy Posutrepedic. It features a twice tempered Posturetech coil unit, and unicased edge support.</p> <p> &nbsp;</p> <p> <strong>Main Features</strong></p> <ul> <li> No flip construction</li> <li> Posturetech coil unit</li> <li> Twice tempered steel coils for strength and durability</li> <li> Limited deflexion boxspring to extend comfort like</li>asdfjk<li <li> Flamegaurd fiber protection</li> <li> Unicased high density edge support</li> <li> 3 zone pressure relief inlay featuring super soft Sealy foam</li> <li> Corner gaurds on boxspring for added durability</li> </ul> <p> &nbsp;</p> <p> <strong>Mattress and Foundation Height</strong></p> <ul> <li> Mattress Height: 11 1/2&quot;</li> <li> Standard Foundation Height: 9&rdquo;</li> <li> Low Profile Foundation Height: 5 &rdquo;</li> </ul> <p> &nbsp;</p> <p> <strong>Warranty</strong></p> <ul> <li> 10&nbsp;years</li> </ul> <p> &nbsp;</p> <p> <strong>Size Information</strong></p> <table border=&quot;0&quot; cellpadding=&quot;3&quot; cellspacing=&quot;3&quot; width=&quot;250&quot;> <tbody> <tr> <td> <strong>Twin</strong></td> <td> 39&quot;X75&quot;</td> <td> 420&nbsp;Coils</td> </tr> <tr> <td> <strong>Twin XL</strong></td> <td> 39&quot;X80&quot;</td> <td> 448&nbsp;Coils</td> </tr> <tr> <td> <strong>Full</strong></td> <td> 54&quot;X75&quot;</td> <td> 600&nbsp;Coils</td> </tr> <tr> <td> <strong>Full Xl</strong></td> <td> 54&quot;X80&quot;</td> <td> 640&nbsp;Coils</td> </tr> <tr> <td> <p> <strong>Queen</strong></p> </td> <td> 60&quot;X80&quot;</td> <td> 736&nbsp;Coils</td> </tr> <tr> <td> <strong>King</strong></td> <td> 76&quot;X80&quot;</td> <td> 928&nbsp;Coils</td> </tr> <tr> <td> <strong>Cal King</strong></td> <td> 72&quot;X84&quot;</td> <td> 924 Coils</td> </tr> </tbody> </table> <p>&nbsp;</p> <p>";
		// final String html =
		// "Enjoy crystal-clear,ideo WinDVD 8 is the world's #1 viewing experience.Fun Extra with Color Themes that match your mood or your desktop. And use Instant ReNEW! \"film look\" p of screen.<li";
		// final String html =
		// "Simultaneously playful and we l.<li>Hematite-colored brass.</li><li>Black pave glass flower face with large faceted glass bead at center.</li><li>1 1/2\"W.</li><li>Imported.</li></ul>";
		// final String html =
		// "<ul>The iconic John Hardy chain texture frames naturally colored turquoise on this boldly on-trend statement piece. Its versatile color and elegant design wear well with both day and evening looks. <li>From the Classic Chain Collection.</li><li>Sterling silver.</li><li>Signature woven chain texture.</li><li>Large square pendant with turquoise inlay.</li><li>2 1/8\" x 1 1/4\".</li><li>Chain sold separately.</li><li>Handcrafted in Bali.</li></ul>About this collection:<br><br>The Classic Chain collectiona signature John Hardy motifincludes chains that are hand-woven, link by link, and annealed for a smooth and supple drape that follows the contours of the body. Each piece is made by a single artisan and is carved by hand in jeweler's wax by master artisans.";
		final String html = "This restorative treatment improves the appearance of fine lines, wrinkles, darkness and puffiness in the delicate eye area. <br><br>What it Does:<ul><li>Stimulates Collagen ProductionImproves skin density; reduces fine lines, wrinkles and sagging.</li><li>Controls Melanin ProductionImproves skin clarity and promotes an even complexion.</li><li>Improves Hydration LevelsIncreases skin radiance, smoothness and firmness.</li></ul>New Technology<br><br>TIME RESPONSE Complex<li>Consists of Green Tea Stem Cells, Green Tea EGCG and Green Tea Saponin.</li><li>Stimulates essential genes to restore optimal skin functions.</li></ul>Microfluidic Delivery System<ul><li>24-hour time-release delivery system developed by Dr. David Weitz Research Group at Harvard University.</li><li>Provides optimal penetration of powerful ingredients.</li></ul> 63 Active Botanical Ingredients:<li>Potent botanical ingredients deeply moisturize, correct hyper-pigmentation, reduce inflammation, increase cellular turnover and provide antioxidant protection.</li></ul>";

		// String html2 =
		// "<html><body><p>paragraph at start</p>hello<br />What is happening?<p>this is a<br />mutiline paragraph</p><ol>  <li>This</li>  <li>is</li>  <li>an</li>  <li>ordered</li>  <li>list    <p>with</p>    <ul>      <li>another</li>      <li>list        <dl>          <dt>This</dt>          <dt>is</dt>            <dd>sdasd</dd>            <dd>sdasda</dd>            <dd>asda              <p>aasdas</p>            </dd>            <dd>sdada</dd>          <dt>fsdfsdfsd</dt>        </dl>        <dl>          <dt>vbcvcvbcvb</dt>          <dt>cvbcvbc</dt>            <dd>vbcbcvbcvb</dd>          <dt>cvbcv</dt>          <dt></dt>        </dl>        <dl>          <dt></dt>        </dl></li>      <li>cool</li>    </ul>    <p>stuff</p>  </li>  <li>cool</li></ol><p></p></body></html>";
		System.out.println(convert(html));
	}
}
