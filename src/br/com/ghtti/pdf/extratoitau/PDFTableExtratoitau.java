package br.com.ghtti.pdf.extratoitau;

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

public class PDFTableExtratoitau {
	private Table table;
	private Document document;

	static final String[] header = { "", "Data", "Histórico de Lançamentos", "Orig", "Valor (R$)", "", "Saldo (R$)", "",
			"HP Lote FI", "Cd.Transferência", "DtVl.", "DtComp.", "TM.", "Terminal", "Num Caixa", "Transação", "Auten",
			"ID.", "Cart.", "" };

	protected PDFTableExtratoitau(String extrato, Document document, PdfDocument pdf, boolean admin) throws IOException {

		if (admin) {
			table = new Table(new float[] { 4f, 20f, 95f, 20f, 78f, 4f, 78f, 4f, 40f, 55f, 25f, 35f, 20f, 32f, 37f, 36f,
					25f, 14f, 25f, 4f }, true);
			table.setWidth(UnitValue.createPercentValue(100));
		} else {
			table = new Table(new float[] { 8f, 30f, 95f, 20f, 80f, 4f, 80f, 4f, 8f }, true);
			table.setWidth(UnitValue.createPercentValue(98));
		}

		// Cria todo o cabeçalho
		PDFHeaderExtratoitau.documentHeaderItau(extrato, pdf, admin);
		// Adiciona tabela ao documento
		setDocument(document.add(table));
		// Popular tabela cliente ou administrativa
		processTable(extrato, table, admin);
		// Conclui a tabela
		//getDocument().close();
		
	}

	private void processTable(String extrato, Table table, boolean adm) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		float intensidadeCor = 0.06f;
		float fontSize = 7f;
		Color gray = new DeviceCmyk(intensidadeCor, intensidadeCor, intensidadeCor, intensidadeCor);
		String valor;

		Image esq = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_esq.jpg")));
		Image dir = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_dir.jpg")));
		Image cen = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader().getResource("br/com/ghtti/documentos/imagens/barra_centro.jpg")));
		

		for (int i = 0; i < PDFTableExtratoitau.header.length; i++) {
			if (i == 8 && !adm) {
				Cell direita = new Cell();
				direita.setNextRenderer(new ImageBackgroundCellRenderer(direita.setBorder(Border.NO_BORDER), dir));
				table.addHeaderCell(direita.setPadding(3.5f).setHeight(0f).setWidth(0f));
				break;
			}

			Cell head = new Cell();
			head.setBorder(Border.NO_BORDER);
			head.add(new Paragraph(PDFTableExtratoitau.header[i]));
			head.setFont(bold).setFontSize(fontSize);
			if (i == 0) {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, esq));
			} else if (i == 19) {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, dir));
			} else {
				head.setNextRenderer(new ImageBackgroundCellRenderer(head, cen));
			}
			table.addHeaderCell(head.setPadding(3.5f));
		}

		table.getHeader().getCell(0, 1).setTextAlignment(TextAlignment.LEFT);
		table.getHeader().getCell(0, 2).setTextAlignment(TextAlignment.LEFT);
		table.getHeader().getCell(0, 4).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(0f);
		table.getHeader().getCell(0, 6).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(0f);

		table.getHeader().setMarginTop(86f);
		int nroPosicaoLancamento = 0;

		Matcher match = Pattern.compile("([0-3][0-9]/[0-3][0-9].*)").matcher(extrato);

		while (match.find()) {
			
			String linhaLancamento = match.group(1);
			int numberOfRows = table.getNumberOfRows();

			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula vazia da esquerda

			valor = getValueFromLancamento(linhaLancamento, 0, 5);// DATA:
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
			valor = getValueFromLancamento(linhaLancamento, 9, 34);// LANCAMENTO
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
			valor = getValueFromLancamento(linhaLancamento, 35, 39);// ORIG
			createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);

			valor = getValueFromLancamento(linhaLancamento, 39, 60);// VALOR
			createAndAddValorESaldoCelltoTable(table, gray, font, fontSize, numberOfRows, valor);

			valor = getValueFromLancamento(linhaLancamento, 60, 83);// SALDO
			createAndAddValorESaldoCelltoTable(table, gray, font, fontSize, numberOfRows, valor);
			
			if (!adm) {
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula vazia da direita
				if (table.getNumberOfRows() % 55 == 0) {
					table.flush();
					getDocument().add(new AreaBreak());
				}
			} else {

				valor = getValueFromLancamento(linhaLancamento, 84, 95);// HP LOTE
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 96, 113);// Transferencia
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 114, 119);// Dtvl
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 120, 125);// DtComp.
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 126, 128);// TM.
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 129, 137);// Terminal
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 138, 148);// Num Caixa
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 149, 156);// Transacao
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 158, 163);// Auten
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 164, 166);// ID
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				valor = getValueFromLancamento(linhaLancamento, 167, 173);// Cart
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, valor);
				
				createAndAddCelltoTable(table, gray, font, fontSize, ColorConstants.BLACK, numberOfRows, ""); // Celula vazia da direita

				if (table.getNumberOfRows() % 39 == 0) {
					table.flush();
					getDocument().add(new AreaBreak());
				}
			}
			nroPosicaoLancamento++;
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

	private void createAndAddValorESaldoCelltoTable(Table table, Color gray, PdfFont font, float fontSize, int posicaoRow,
			String valor) {
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