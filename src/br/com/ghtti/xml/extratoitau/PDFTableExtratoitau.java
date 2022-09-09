package br.com.ghtti.xml.extratoitau;

import java.io.IOException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	public PDFTableExtratoitau(org.w3c.dom.Document xml, Document document, PdfDocument pdf, boolean admin)
			throws IOException {

		if (admin) {
			table = new Table(new float[] { 4f, 20f, 95f, 20f, 78f, 4f, 78f, 4f, 40f, 55f, 25f, 35f, 20f, 32f, 37f, 36f,
					25f, 14f, 25f, 4f }, true);
			table.setWidth(UnitValue.createPercentValue(100));
		} else {
			table = new Table(new float[] { 8f, 30f, 95f, 20f, 80f, 4f, 80f, 4f, 8f }, true);
			table.setWidth(UnitValue.createPercentValue(98));
		}

		// Cria todo o cabeçalho
		PDFHeaderExtratoitau.documentHeaderItau(xml, pdf, admin);
		// Adiciona tabela ao documento
		setDocument(document.add(table));
		// Popular tabela cliente ou administrativa
		XMLTableBuilder(xml, table, admin);
		// Conclui a tabela
		//getDocument().close();
	}

	private void XMLTableBuilder(org.w3c.dom.Document xml, Table table, boolean adm) throws IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		float intensidadeCor = 0.06f;
		Color gray = new DeviceCmyk(intensidadeCor, intensidadeCor, intensidadeCor, intensidadeCor);

		float fontSize = 7f;
		float padding = 0.1f;

		Image esq = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader()
				.getResource("br/com/ghtti/documentos/imagens/barra_esq.jpg")));
		Image dir = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader()
				.getResource("br/com/ghtti/documentos/imagens/barra_dir.jpg")));
		Image cen = new Image(ImageDataFactory.create(PDFTableExtratoitau.class.getClassLoader()
				.getResource("br/com/ghtti/documentos/imagens/barra_centro.jpg")));

		NodeList clienteList = xml.getElementsByTagName("Lancamento");

		if (adm)
			fontSize = 7f;

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

		for (int temp = 0; temp < clienteList.getLength(); temp++) {

			Node nNode = clienteList.item(temp);
			if (nNode.getFirstChild().getNodeName().equals("Data")) {
				Cell cell1 = new Cell();
				cell1.setBorder(Border.NO_BORDER);
				if (table.getNumberOfRows() % 2 == 1)
					cell1.setBackgroundColor(gray);
				cell1.add(new Paragraph(""));
				table.addCell(cell1.setPaddingTop(padding).setPaddingBottom(padding));
			}
			NodeList filhos = nNode.getChildNodes();

			for (int y = 0; y < filhos.getLength(); y++) {

				Node fio = filhos.item(y);

				if (fio.getNodeType() == Node.ELEMENT_NODE && (fio.getNodeName().equals("Data")
						|| ((fio.getNodeName().equals("Lancamento")) && fio.hasChildNodes())
						|| fio.getNodeName().equals("Orig") || fio.getNodeName().equals("Valor")
						|| fio.getNodeName().equals("Saldo")
						|| (adm && (fio.getNodeName().equals("HPLoteFI") || fio.getNodeName().equals("CDTransferencia")
								|| fio.getNodeName().equals("DtVl") || fio.getNodeName().equals("DtComp")
								|| fio.getNodeName().equals("TM") || fio.getNodeName().equals("Terminal")
								|| fio.getNodeName().equals("NumCaixa") || fio.getNodeName().equals("Transacao")
								|| fio.getNodeName().equals("Auten") || fio.getNodeName().equals("ID")
								|| fio.getNodeName().equals("Cart"))))) {
					String valor = fio.getTextContent().trim();
					Cell cell = new Cell();
					cell.setBorder(Border.NO_BORDER);
					if (table.getNumberOfRows() % 2 == 0) {
						cell.setBackgroundColor(gray);
					}
					if ((fio.getNodeName().equals("Valor") || fio.getNodeName().equals("Saldo"))) {
						if (valor.length() == 0) {
						} else if (valor.charAt(valor.length() - 1) == '-') {
							cell.add(new Paragraph(valor.substring(0, valor.length() - 1))
									.setFontColor(ColorConstants.RED).setFontSize(fontSize)
									.setTextAlignment(TextAlignment.RIGHT));
						} else {
							cell.add(new Paragraph(valor.substring(0, valor.length())).setFontSize(fontSize)
									.setPadding(0f).setTextAlignment(TextAlignment.RIGHT));
						}
					} else {
						cell.add(new Paragraph(valor).setFont(font).setFontSize(fontSize));
					}

					table.addCell(cell.setPaddingTop(padding).setPaddingBottom(padding));

					if ((fio.getNodeName().equals("Valor") || fio.getNodeName().equals("Saldo"))) {
						Cell negat = new Cell();
						negat.setBorder(Border.NO_BORDER);
						if (table.getNumberOfRows() % 2 == 0)
							negat.setBackgroundColor(gray);
						negat.setPaddingLeft(0f);
						if (valor.length() != 0) {
							if (valor.charAt(valor.length() - 1) == '-') {
								negat.add(new Paragraph("-")).setFont(font).setFontColor(ColorConstants.RED)
										.setFontSize(fontSize);
								negat.setPaddingLeft(0f);
							} else {
								negat.add(new Paragraph(""));
							}
						}
						table.addCell(negat.setPaddingTop(padding).setPaddingBottom(padding));
					}
				}
			}
			if (nNode.getFirstChild().getNodeName().equals("Data")) {
				Cell ultimaColunaFechamento = new Cell();
				ultimaColunaFechamento.setBorder(Border.NO_BORDER);

				if (table.getNumberOfRows() % 2 == 0)
					ultimaColunaFechamento.setBackgroundColor(gray);

				table.addCell(ultimaColunaFechamento.setMaxHeight(0f).setMinHeight(0f).setPaddingTop(0));
				if (!adm && table.getNumberOfRows() % 55 == 0) {
					table.flush();
					getDocument().add(new AreaBreak());
				} else if (adm && table.getNumberOfRows() % 39 == 0) {
					table.flush();
					getDocument().add(new AreaBreak());
				}
			}
		}
		table.complete();
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}