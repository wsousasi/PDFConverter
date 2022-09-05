package br.com.ghtti.pdf.extratociti;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import br.com.ghtti.pdf.extrato.PDFDocument;

public class PDFDocumentCiti extends PDFDocument {

	public PDFDocumentCiti() {
		super();
	}

	@Override
	public void setPDFProperties() {
		setBaos(new ByteArrayOutputStream());
		setPdfWriter(new PdfWriter(getBaos()));
		setPdfDoc(new PdfDocument(getPdfWriter()));
		setDocument(new com.itextpdf.layout.Document(getPdfDoc(), PageSize.A4));
		getDocument().setMargins(20, 20, 20, 20);
	}

	@Override
	public ByteArrayOutputStream getPDFDocument(String extrato, boolean bVisaoBanco) throws IOException {
		new PDFTableCiti(extrato, getDocument(), getPdfDoc(), bVisaoBanco);
		getBaos().close();
		return getBaos();
	}
}