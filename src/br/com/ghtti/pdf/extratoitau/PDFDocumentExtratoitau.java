package br.com.ghtti.pdf.extratoitau;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import br.com.ghtti.pdf.extrato.PDFDocument;

public class PDFDocumentExtratoitau extends PDFDocument {

	public PDFDocumentExtratoitau() {
		super();
	}

	@Override
	public void setPDFProperties(boolean bVisaoBanco) {
		setBaos(new ByteArrayOutputStream());
		setPdfWriter(new PdfWriter(getBaos()));
		setPdfDoc(new PdfDocument(getPdfWriter()));
		if (bVisaoBanco) {
			setDocument(new com.itextpdf.layout.Document(getPdfDoc(), PageSize.A4.rotate()));
		} else {
			setDocument(new com.itextpdf.layout.Document(getPdfDoc(), PageSize.A4));
		}
		getDocument().setMargins(20, 20, 20, 20);
	}

	@Override
	public ByteArrayOutputStream getPDFDocument(String extrato, int paginasPorPDF, boolean bVisaoBanco) throws IOException {
		new PDFTableExtratoitau(extrato, getDocument(), getPdfDoc(), bVisaoBanco);
		
		int totalPaginasDoc = getPdfDoc().getNumberOfPages();
		
		if(totalPaginasDoc == paginasPorPDF + 1) {
			getPdfDoc().removePage(totalPaginasDoc);
		}
		
		getPdfDoc().close();
		getBaos().close();
		return getBaos();
	}
}