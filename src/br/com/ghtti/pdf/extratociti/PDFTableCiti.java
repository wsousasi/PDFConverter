package br.com.ghtti.pdf.extratociti;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import br.com.ghtti.pdf.extrato.ImageBackgroundCellRenderer;

public class PDFTableCiti {
	private Table table;
	private Document document;

	static final String[] header = { "", "Data", "Histórico de Lançamentos", "Orig", "Valor (R$)", "", "Saldo (R$)", "",
			"Referência", "" };

	public PDFTableCiti(String extrato, Document document, PdfDocument pdf, boolean admin) throws IOException {

		if (admin)
			table = new Table(new float[] { 7f, 30f, 100f, 20f, 80f, 4f, 80f, 4f, 32f, 7f }, true);
		else
			table = new Table(new float[] { 7f, 30f, 100f, 20f, 80f, 4f, 80f, 4f, 7f }, true);
		table.setWidth(UnitValue.createPercentValue(97));

		// Cria todo o cabeçalho
		PDFHeaderCiti.documentHeaderCiti(extrato, pdf);
		// Adiciona tabela ao documento
		setDocument(document.add(table));
		// Popular tabela cliente ou administrativa
		processTable(extrato, table, admin);
		// Conclui a tabela
		getDocument().close();
	}

	private void processTable(String extrato, Table table, boolean adm) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		
		float intensidadeCor = 0.06f;
		Color gray = new DeviceCmyk(intensidadeCor, intensidadeCor, intensidadeCor, intensidadeCor);
		float fontSize = 7f;
		String valor;

		Image esq = new Image(ImageDataFactory.create(PDFTableCiti.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_esq.jpg")));
		Image dir = new Image(ImageDataFactory.create(PDFTableCiti.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_dir.jpg")));
		Image cen = new Image(ImageDataFactory.create(PDFTableCiti.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_centro.jpg")));

		for (int i = 0; i < header.length; i++) {
			if (i == 8 && !adm) {
				Cell direita = new Cell();
				direita.setNextRenderer(new ImageBackgroundCellRenderer(direita, dir));
				table.addHeaderCell(direita.setBorder(Border.NO_BORDER));
				break;
			}
			Cell head = new Cell();
			head.add(new Paragraph(header[i]));
			head.setFont(bold).setFontSize(fontSize);
			if (i == 0) {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, esq));
			} else if (i == 9) {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, dir));

			} else {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, cen));
			}
			table.addHeaderCell(head.setBorder(Border.NO_BORDER));
		}

		table.getHeader().getCell(0, 1).setTextAlignment(TextAlignment.LEFT);
		table.getHeader().getCell(0, 2).setTextAlignment(TextAlignment.LEFT);
		table.getHeader().getCell(0, 4).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(0f);
		table.getHeader().getCell(0, 6).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(0f);

		table.getHeader().setMarginTop(86f);

		Matcher match = Pattern.compile("([0-3][0-9]/[0-3][0-9].*)").matcher(extrato);



		while (match.find()) {

			String linhaLancamento = match.group(1);
			int numberOfRows = table.getNumberOfRows();

			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula vazia da esquerda

			valor = getValueFromLancamento(linhaLancamento, 0, 5);// DATA:
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
			valor = getValueFromLancamento(linhaLancamento, 9, 35);// DESCRICAO:
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
			valor = getValueFromLancamento(linhaLancamento, 35, 40);// ORIG:
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);

			valor = getValueFromLancamento(linhaLancamento, 40, 60);// VALOR:
			createAndAddValorESaldoCelltoTable(table, gray, font, fontSize, numberOfRows, valor);

			valor = getValueFromLancamento(linhaLancamento, 61, 83);// SALDO:
			createAndAddValorESaldoCelltoTable(table, gray, font, fontSize, numberOfRows, valor);

			if (!adm) {
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula vazia da direita
			} else {
				valor = getValueFromLancamento(linhaLancamento, 84, 91);// REFERENCIA:
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula	vazia da direita
			}
			
			if (table.getNumberOfRows() % 59 == 0) {
				table.flush();
				getDocument().add(new AreaBreak());
			}
		}
		table.complete();
	}

	private void createAndAddCelltoTable(Table table, Color gray, PdfFont font, float fontSize, Color fontColor,
			int posicaoRow, String valor) {
		Cell celula = new Cell().setBorder(Border.NO_BORDER);
		if (posicaoRow % 2 == 1)
			celula.setBackgroundColor(gray);
		if (valor.equalsIgnoreCase("-"))
			celula.setPaddingLeft(0);
		celula.add(new Paragraph(valor).setFontSize(fontSize).setFontColor(fontColor));
		table.addCell(celula.setPaddingTop(0.1f).setPaddingBottom(0.1f));
	}

	private void createAndAddValorESaldoCelltoTable(Table table, Color gray, PdfFont font, float fontSize,
			int posicaoRow, String valor) {
		Cell celula = new Cell().setBorder(Border.NO_BORDER);
		celula.setBorder(Border.NO_BORDER);
		if (posicaoRow % 2 == 1)
			celula.setBackgroundColor(gray);

		if (valor.length() == 0) {
			celula.add(new Paragraph(""));
		} else if (valor.charAt(valor.length() - 1) == '-') {
			celula.add(new Paragraph(valor.substring(0, valor.length() - 1)).setFontColor(ColorConstants.RED)
					.setFontSize(fontSize).setTextAlignment(TextAlignment.RIGHT));
		} else {
			celula.add(new Paragraph(valor.substring(0, valor.length())).setFontSize(fontSize).setPadding(0f)
					.setTextAlignment(TextAlignment.RIGHT));
		}

		table.addCell(celula.setPaddingBottom(0.1f).setPaddingTop(0.1f));

		if (valor.length() != 0 && valor.charAt(valor.length() - 1) == '-')
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.RED, posicaoRow, "-");
		else
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, posicaoRow, "");
	}

	public String getValueFromLancamento(String valor, int inicio, int fim) {
		try {
			return valor.substring(inicio, fim);
		} catch (Exception e) {
			try {
				return valor.substring(inicio, fim - 1);
			} catch (Exception e2) {
				return "";
			}
		}
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}