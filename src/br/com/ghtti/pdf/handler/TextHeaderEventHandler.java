package br.com.ghtti.pdf.handler;

import java.io.IOException;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

public class TextHeaderEventHandler implements IEventHandler {
	Paragraph pHeader;
	Paragraph pAgencia;
	Paragraph pConta;
	Paragraph pNome;
	Paragraph pData;
	Paragraph pNumPag;
	int posicaoY = 744;

	//ContaItau
	public TextHeaderEventHandler(int posicaoY, String agencia, String conta, String nome, String data, String numPag)
			throws IOException {
		pHeader = new Paragraph("Agência:                Conta:                              Nome:")
				.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9)
				.setFontColor(ColorConstants.BLACK);
		this.pAgencia = new Paragraph(agencia).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
				.setFontSize(7).setFontColor(ColorConstants.BLACK);
		this.pConta = new Paragraph(conta).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		this.pNome = new Paragraph(nome).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		this.pData = new Paragraph(data).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		this.pNumPag = new Paragraph(numPag).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		this.posicaoY = posicaoY;
	}
	//ContaCiti
	public TextHeaderEventHandler(String agencia, String conta, String nome, String data) throws IOException {
		pHeader = new Paragraph("Agência:                Conta:                              Nome:")
				.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(9)
				.setFontColor(ColorConstants.BLACK);
		pAgencia = new Paragraph(agencia).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		pConta = new Paragraph(conta).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		pNome = new Paragraph(nome).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
		pData = new Paragraph(data).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(7)
				.setFontColor(ColorConstants.BLACK);
	}

	@Override
	public void handleEvent(Event event) {

		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfDocument pdf = docEvent.getDocument();
		PdfPage page = docEvent.getPage();
		Rectangle pageSize = page.getPageSize();

		float y = pageSize.getTop() - 82;

		PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
		@SuppressWarnings("resource")
		Canvas canvas = new Canvas(pdfCanvas, pageSize);
		canvas.showTextAligned(pHeader, 20, y, TextAlignment.LEFT, VerticalAlignment.BOTTOM);

		canvas.showTextAligned(pAgencia, 20, posicaoY, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
		canvas.showTextAligned(pConta, 98, posicaoY, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
		canvas.showTextAligned(pNome, 200, posicaoY, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
		canvas.showTextAligned(pData, 390, posicaoY, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
		if(pNumPag != null)
		canvas.showTextAligned(pNumPag, 480, posicaoY, TextAlignment.LEFT, VerticalAlignment.BOTTOM);
	}
}