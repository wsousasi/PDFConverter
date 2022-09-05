package br.com.ghtti.documentos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Document;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import br.com.ghtti.pdf.extrato.PDFDocument;
import br.com.ghtti.pdf.extratociti.PDFDocumentCiti;
import br.com.ghtti.pdf.extratoitau.PDFDocumentExtratoitau;

/**
 *
 * @author Weslley Sousa
 * @version 1.0
 * @build: 04/09/2020
 * 
 *         Classe responsável por gerar o documento PDF.
 *
 */
public class PDFGenerator {

	protected static byte[] createPDF(List<Documento> documentos, String pdfTemplateFile, boolean base64) {
		ByteArrayOutputStream baosPdfW;
		PdfDocument pdfDocumentR;
		ByteArrayOutputStream pdfResultante = new ByteArrayOutputStream();
		PdfDocument pdfDocumentW = new PdfDocument(new PdfWriter(pdfResultante));

		try {
			for (Documento documento : documentos) {
				baosPdfW = new ByteArrayOutputStream();

				if (!new File(pdfTemplateFile).exists()) {
					System.out.println(
							"PDFGenerator.createPDF() > Nao foi encontrado o template PDF '" + pdfTemplateFile + "'");
				}

				pdfDocumentR = new PdfDocument(new PdfReader(pdfTemplateFile), new PdfWriter(baosPdfW));
				PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocumentR, false);
				String value, key;

				for (Entry<String, String> entry : documento.campos.entrySet()) {
					value = entry.getValue();
					key = entry.getKey();
					System.out.println("form1[0].P1[0]." + key + "[1]" + "value: " + value);
					form.getField("form1[0].P1[0]." + key + "[0]").setValue(value);
					if (form.getField("form1[0].P1[0]." + key + "[1]") != null) {
						form.getField("form1[0].P1[0]." + key + "[1]").setValue(value);
					}
				}
				form.flattenFields();
				pdfDocumentR.close();
				pdfDocumentR = new PdfDocument(new PdfReader(new ByteArrayInputStream(baosPdfW.toByteArray())));
				pdfDocumentR.copyPagesTo(1, pdfDocumentR.getNumberOfPages(), pdfDocumentW, new PdfPageFormCopier());
				pdfDocumentR.close();
			}
			pdfDocumentW.close();
			if (base64)
				return java.util.Base64.getEncoder().encode(pdfResultante.toByteArray());
			else
				return pdfResultante.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Exportar a partir de XML
	public static byte[] buildPDFExtratoC(Document xml, String gaveta, boolean bVisaoBanco) throws IOException {
		PDFDocument pdfDoc = null;
		if (gaveta.equalsIgnoreCase("ExtratoCCCiti")) {
			pdfDoc = new PDFDocumentCiti();
			pdfDoc.setPDFProperties();
		} else if (gaveta.equalsIgnoreCase("Extrato Itau") || (gaveta.equalsIgnoreCase("Extrato Poupanca Itau"))) {
			pdfDoc = new PDFDocumentExtratoitau();
			pdfDoc.setPDFProperties(bVisaoBanco);
		}
		return pdfDoc.getPDFDocument(xml, bVisaoBanco).toByteArray();
	}
	//Exportar a partir de TXT
	public static byte[] buildPDFExtrato(String texto, String gaveta, int paginasPorPDF, boolean bVisaoBanco) throws IOException {
		PDFDocument pdfDoc = null;
		if (gaveta.equalsIgnoreCase("ExtratoCCCiti")) {
			pdfDoc = new PDFDocumentCiti();
			pdfDoc.setPDFProperties();
		} else if (gaveta.equalsIgnoreCase("Extrato Itau") || (gaveta.equalsIgnoreCase("Extrato Poupanca Itau"))) {
			pdfDoc = new PDFDocumentExtratoitau();
			pdfDoc.setPDFProperties(bVisaoBanco);
		}
		return pdfDoc.getPDFDocument(texto, paginasPorPDF, bVisaoBanco).toByteArray();
	}
}