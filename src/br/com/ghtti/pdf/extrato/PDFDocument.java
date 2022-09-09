package br.com.ghtti.pdf.extrato;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

public abstract class PDFDocument {
	private ByteArrayOutputStream baos;
	private PdfWriter pdfWriter;
	private PdfDocument pdfDoc;
	private com.itextpdf.layout.Document document;
	private int numeroDePaginasTotal;

	public void setPDFProperties(boolean bVisaoBanco) {
	}

	public void setPDFProperties() {
	}

	public ByteArrayOutputStream getPDFDocument(Document xml, int paginasPorPDF, boolean bVisaoBanco) throws IOException {
		return null;
	}
	
	public ByteArrayOutputStream getPDFDocument(String texto, int paginasPorPDF, boolean bVisaoBanco) throws IOException {
		return null;
	}

	public ByteArrayOutputStream getBaos() {
		return baos;
	}

	public void setBaos(ByteArrayOutputStream baos) {
		this.baos = baos;
	}

	public PdfWriter getPdfWriter() {
		return pdfWriter;
	}

	public void setPdfWriter(PdfWriter pdfWriter) {
		this.pdfWriter = pdfWriter;
	}

	public PdfDocument getPdfDoc() {
		return pdfDoc;
	}

	public void setPdfDoc(PdfDocument pdfDoc) {
		this.pdfDoc = pdfDoc;
	}

	public com.itextpdf.layout.Document getDocument() {
		return document;
	}

	public void setDocument(com.itextpdf.layout.Document document) {
		this.document = document;
	}
}