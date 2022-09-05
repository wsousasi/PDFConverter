package br.com.ghtti.pdf.handler;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;


public class ImageEventHandler implements IEventHandler {
	protected Image img;

	public ImageEventHandler(Image img) {
		this.img = img;
	}

	@SuppressWarnings("resource")
	@Override
	public void handleEvent(Event event) {
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfDocument pdfDoc = docEvent.getDocument();
		PdfPage page = docEvent.getPage();
		PdfCanvas aboveCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
		Rectangle area = page.getPageSize();
		new Canvas(aboveCanvas, area).add(img);
	}
}