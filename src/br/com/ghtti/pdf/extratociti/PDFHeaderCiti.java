package br.com.ghtti.pdf.extratociti;

import java.io.IOException;
import java.util.Scanner;

import org.w3c.dom.DOMException;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;

import br.com.ghtti.pdf.handler.ImageEventHandler;
import br.com.ghtti.pdf.handler.TextHeaderEventHandler;

public class PDFHeaderCiti {

	public static void documentHeaderCiti(String texto, PdfDocument pdf) throws DOMException, IOException{

		String ContextAgencia = "";
		String ContextConta = "";
		String ContextName = "";
		String ContextAno = "";
		
		Scanner lerTexto = new Scanner(texto);
		String cabecalho = "";
		
		for (int linhaPosicao = 0; linhaPosicao < 5; linhaPosicao++) {
			cabecalho = lerTexto.nextLine();

			if(linhaPosicao == 4) {
				ContextAgencia = cabecalho.substring(1, 5).trim();
				ContextConta = cabecalho.substring(10, 20).trim();
				ContextName = cabecalho.substring(26, 80).trim();
				ContextAno = cabecalho.substring(80, cabecalho.length()).trim();
			}
		}

		pdf.addEventHandler(PdfDocumentEvent.START_PAGE,
				new TextHeaderEventHandler(ContextAgencia, ContextConta, ContextName, ContextAno));
		
		ImageEventHandler handler = new ImageEventHandler(new Image(ImageDataFactory.create(
				PDFHeaderCiti.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/topo_citi.png")))
						.setFixedPosition(15, 784).setWidth(550).setHeight(30));
		pdf.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
	}
}