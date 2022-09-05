package br.com.ghtti.pdf.extratoitau;

import java.io.IOException;
import java.util.Scanner;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;

import br.com.ghtti.pdf.handler.ImageEventHandler;
import br.com.ghtti.pdf.handler.TextHeaderEventHandler;

public class PDFHeaderExtratoitau {

	public static void documentHeaderItau(String texto, PdfDocument pdf, boolean bVisaoBanco)
			throws IOException {

		Scanner lerTexto = new Scanner(texto);
		String cabecalho = "";

		String ContextAgencia = "";
		String ContextConta = "";
		String ContextName = "";
		String ContextAno = "";
		String ContextNumPag = "";
		String tipoForm = "";

		for (int linhaPosicao = 0; linhaPosicao < 3; linhaPosicao++) {
			cabecalho = lerTexto.nextLine();
			if (cabecalho.contains("$DJDE$"))
				tipoForm = cabecalho.substring(11, 25).trim();
			if(linhaPosicao == 2) {
				ContextAgencia = cabecalho.substring(1, 5).trim();
				ContextConta = cabecalho.substring(10, 18).trim();
				ContextName = cabecalho.substring(25, 56).trim();
				ContextAno = cabecalho.substring(57, 72).trim();
				ContextNumPag = cabecalho.substring(cabecalho.length()-9,cabecalho.length()).trim();
			}
		}

		int posicaoY = 744;
		if (bVisaoBanco)
			posicaoY = 498;

		// Popular cabeçalho Agencia|Conta|Nome|Ano|NumeroPag
		TextHeaderEventHandler textHandler = new TextHeaderEventHandler(posicaoY, ContextAgencia, ContextConta,
				ContextName, ContextAno, ContextNumPag);

		Image image =	createImage(tipoForm,bVisaoBanco);

		pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new ImageEventHandler(image));
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