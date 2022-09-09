package br.com.ghtti.xml.extratoitau;

import java.io.IOException;

import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;

import br.com.ghtti.pdf.handler.ImageEventHandler;
import br.com.ghtti.pdf.handler.TextHeaderEventHandler;

public class PDFHeaderExtratoitau {

	public static void documentHeaderItau(org.w3c.dom.Document xml, PdfDocument pdf,
			boolean bVisaoBanco) throws DOMException, IOException {

		NodeList ContextAgencia = xml.getElementsByTagName("Agencia");
		NodeList ContextConta = xml.getElementsByTagName("NConta");
		NodeList ContextName = xml.getElementsByTagName("Nome");
		NodeList ContextAno = xml.getElementsByTagName("MesAno");
		NodeList ContextNumPag = xml.getElementsByTagName("NroPag");

		int posicaoY = 744;
		if (bVisaoBanco)
			posicaoY = 498;

		// Popular cabeçalho Agencia|Conta|Nome|Ano|NumeroPag
		TextHeaderEventHandler textHandler = new TextHeaderEventHandler(posicaoY,
				ContextAgencia.item(0).getTextContent(), ContextConta.item(0).getTextContent(),
				ContextName.item(0).getTextContent(), ContextAno.item(0).getTextContent(),
				ContextNumPag.item(0).getTextContent());

		Image image = createImage(xml.getElementsByTagName("TipoForm").item(0).getTextContent(), bVisaoBanco);

		pdf.addEventHandler(PdfDocumentEvent.START_PAGE,  new ImageEventHandler(image));
		pdf.addEventHandler(PdfDocumentEvent.START_PAGE, textHandler);
	}
	
	private static Image createImage(String imagemCabecalho, boolean admin) {
		ImageData imageData = null;

		if (imagemCabecalho.equals("FORM=ITAU000L")) {
			imageData = ImageDataFactory.create(PDFHeaderExtratoitau.class.getClassLoader()
					.getResource("br/com/ghtti/documentos/imagens/topo_uniclass.png"));
		} else if (imagemCabecalho.equals("FORM=ITAU0004")) {
			imageData = ImageDataFactory.create(PDFHeaderExtratoitau.class.getClassLoader()
					.getResource("br/com/ghtti/documentos/imagens/topo_personnalite.jpg"));
		} else if (imagemCabecalho.equals("FORM=ITAU0007")) {
			imageData = ImageDataFactory.create(PDFHeaderExtratoitau.class.getClassLoader()
					.getResource("br/com/ghtti/documentos/imagens/topo_private.png"));
		} else {
			imageData = ImageDataFactory.create(PDFHeaderExtratoitau.class.getClassLoader()
					.getResource("br/com/ghtti/documentos/imagens/topo_varejo.png"));
		}

		if (admin)
			return new Image(imageData).setFixedPosition(15, 537).setWidth(810).setHeight(48);
		else
			return new Image(imageData).setFixedPosition(15, 784).setWidth(550).setHeight(36);
	}
}