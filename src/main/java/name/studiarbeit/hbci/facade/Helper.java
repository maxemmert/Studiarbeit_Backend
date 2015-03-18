package name.studiarbeit.hbci.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class Helper {
	public Document getDocument() {
		try {
			File file = new File("xml/hbci-plus.xml");
			InputStream syntaxStream = new FileInputStream(file);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setIgnoringComments(true);
			dbf.setValidating(false); // TODO: war erst auf true!
			// dbf.setIgnoringElementContentWhitespace(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

			return db.parse(syntaxStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
