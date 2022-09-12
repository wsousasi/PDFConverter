package br.com.ght;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import br.com.ghtti.HandledException.HandledException;
import br.com.ghtti.documentos.PDFGenerator;


/**
 * 
 * @author Weslley Sousa
 * @version 1.0
 * @build: 21/10/2021
 * 
 * Classe utilizada para quebrar extenso arquivo XML para a geração de arquivos PDF - apenas relatórios 'Extrato Itau'
 *
 */

public class OutputHandler {
	//public static byte[] getExtratoPDF(String xml, String gaveta, boolean bVisaoBanco) throws Exception {
	/**
	 * @param documentFullPath Diretorio completo do arquivo XML
	 * @param pdfSaida Diretorio do local de armazenamento do PDF + nome dos arquivos PDF gerados
	 * @param admin Utilizado apenas na gaveta 'Extrato Itau'. Valores aceitos: true = administrador | false = cliente
	 * @param paginasPorPDF Número de páginas para cada arquivo PDF 
	 **/
	public static void getExtratoPDF(String documentFullPath, String pdfSaida, boolean admin, int paginasPorPDF) throws Exception {
		String fileName = "";
		String filePath = "";
		
		if(pdfSaida.length() < 1) {
			System.err.println("Endereço do PDF de saida vazio!");
			return;
		}
		if(documentFullPath.length() < 1) {
			System.out.println("Endereço arquivo de entrada vazio!");
			return;
		}
		
		//new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		if(!(pdfSaida.toLowerCase().startsWith("c:") || pdfSaida.toLowerCase().startsWith("d:"))) {
			filePath = new File(OutputHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			filePath = filePath.substring(0, filePath.length() - 16);
			
			if(pdfSaida.toLowerCase().endsWith(".pdf")) {
				filePath = filePath +"\\"+ pdfSaida.substring(0, pdfSaida.lastIndexOf("\\") + 1);
				fileName = pdfSaida.substring(pdfSaida.lastIndexOf("\\") + 1, pdfSaida.length() - 4);
			}else {
				if(pdfSaida.contains("\\") || pdfSaida.contains("/")) {					
					filePath = filePath + pdfSaida + "\\";
					fileName = documentFullPath.substring(documentFullPath.lastIndexOf("\\") + 1,documentFullPath.length() - 4);
				}else {	
				fileName = pdfSaida;
				}
			}			
		}
		else {
			if(pdfSaida.toLowerCase().endsWith(".pdf")) {
				filePath = pdfSaida.substring(0, pdfSaida.lastIndexOf("\\") + 1);
				fileName = pdfSaida.substring(pdfSaida.lastIndexOf("\\") + 1, pdfSaida.length() - 4);
			}else {
				//if(!pdfSaida.substring(pdfSaida.length() - 1).equals("\\"))
				if(pdfSaida.contains("\\")) {
					filePath = pdfSaida + "\\";
					fileName = documentFullPath.substring(documentFullPath.lastIndexOf("\\") + 1,documentFullPath.length() - 4);
				}
				else {
					fileName = pdfSaida;
				}
			}
		}
		
		System.out.println("Nome do arquivo de saída: "+ fileName);
		System.out.println("Salvar em: "+ filePath);
		if(admin) {
			System.out.println("Visão escolhida: administrativa");
			fileName = fileName + "_[Administrativa]_";
		}
		else {
			System.out.println("Visão escolhida: cliente");
			fileName = fileName + "_[Cliente]_";
		}
		Scanner scanner = new Scanner(new FileReader(documentFullPath));
		int linhaPorPagina;
		
		if(admin)
			linhaPorPagina = 39;
		else
			linhaPorPagina = 55;
		
		int totalDocLinhas = linhaPorPagina*paginasPorPDF;
		System.out.println("Quantidade máxima de páginas por arquivo PDF: " + paginasPorPDF);
		if(documentFullPath.toLowerCase().endsWith(".txt")) {
			System.out.println("Arquivo de texto identificado \nPreparando exportação...");
			exportacaoParaTexto(documentFullPath, filePath, totalDocLinhas, paginasPorPDF, fileName, admin);
		}
		else if(documentFullPath.toLowerCase().endsWith(".xml")) {
			System.out.println("Arquivo XML identificado \nPreparando exportação...");
			exportacaoParaXML(documentFullPath, filePath, totalDocLinhas, paginasPorPDF, fileName, admin);
		}
		else
			throw new HandledException("1", "A extensão do arquivo que você está tentando processar não é compatível");
		
	}
	
	private static String makeHeaderXml(String extrato) throws IOException, HandledException {
		String cabecalho = "";
		String linha = "";
		int contador = 0;
		Scanner lerXml = new Scanner(new FileReader(extrato));
		
		while(lerXml.hasNext()) {
			contador++;
			linha = lerXml.nextLine();
			
			if(linha.startsWith("<Movimentacao>"))
				break;
			
			if(!validadorLinha(linha))
				continue;
			
			cabecalho = cabecalho + linha +"\n";
			if(contador > 20) {
				throw new HandledException("0", "Não foi possivel montar o cabeçalho");
			}				
		}
		
		return cabecalho + "<Movimentacao>";
	}
	
	private static String makeHeaderTexto(String extrato) throws FileNotFoundException {
		String cabecalho = "";
		String linha = "";
		
		Scanner lerTexto = new Scanner(new FileReader(extrato));
		
		for (int linhaPosicao = 0; linhaPosicao < 5; linhaPosicao++) {
			linha = lerTexto.nextLine();
		    if(linha.contains("$DJDE$")){
		    	cabecalho = cabecalho + linha + "\n";
		    	cabecalho = cabecalho + lerTexto.nextLine() + "\n";
		    	cabecalho = cabecalho + lerTexto.nextLine()+ "\n";
		    	cabecalho = cabecalho + lerTexto.nextLine()+ "\n";
		    	cabecalho = cabecalho + lerTexto.nextLine()+ "\n";
		    	cabecalho = cabecalho + lerTexto.nextLine()+ "\n";
		    }
	   }
		lerTexto.close();
		return cabecalho; 
	}
	
	
	
	private static void exportacaoParaXML(String xmlPath, String filePath, int totalDocLinhas, int paginasPorPDF, String fileName, boolean admin ) throws Exception {
		
		String cabecalho = makeHeaderXml(xmlPath);
		//System.out.println(cabecalho);
		int linhaAtual = 0 , numeroDocumentos = 0;
		
		String extrato = cabecalho;
		
		
		Matcher match = Pattern.compile("<Lancamento><Data>.*").matcher(new String(Files.readAllBytes(Paths.get(xmlPath))));
		
		while(match.find()) {
			extrato = extrato + match.group(0)+"\n";
			linhaAtual++;
			
			if(linhaAtual == totalDocLinhas) {
				numeroDocumentos++;
				extrato = extrato + "</Movimentacao> + </Statement>";
				byte[] baReturn = PDFGenerator.buildPDFExtratoC(buildXMLDocument(extrato), "Extrato Itau", paginasPorPDF, admin);
				FileOutputStream fs = new FileOutputStream(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
				fs.write(baReturn);
				fs.close();
				System.out.println(numeroDocumentos+"° exportado com sucesso!!");
				System.out.println(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
				extrato = cabecalho;
				linhaAtual = 0;
			}
		}
		if(!extrato.equalsIgnoreCase(cabecalho)) {
			numeroDocumentos++;
			extrato = extrato + "</Movimentacao> + </Statement>";
			byte[] baReturn = PDFGenerator.buildPDFExtratoC(buildXMLDocument(extrato), "Extrato Itau", paginasPorPDF, admin);
			FileOutputStream fs = new FileOutputStream(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
			fs.write(baReturn);
			fs.close();
			System.out.println(numeroDocumentos+"° exportado com sucesso!!");
			System.out.println(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
		}
		System.out.println("Foi criado um total de: " + numeroDocumentos + " PDF(s)!");
		
	} 
	
	

	private static void exportacaoParaTexto(String textoPath, String filePath, int totalDocLinhas, int paginasPorPDF, String fileName, boolean admin ) throws IOException {
		
		String cabecalho = makeHeaderTexto(textoPath);
		int linhaAtual = 0 , numeroDocumentos = 0;
		
		String extrato = cabecalho;
		
		Matcher match = Pattern.compile("([0-3][0-9]/[0-3][0-9].*)").matcher(new String(Files.readAllBytes(Paths.get(textoPath))));
		
		while (match.find()) {
			extrato = extrato + match.group(1)+"\n";
			linhaAtual++;
			
			if(linhaAtual == totalDocLinhas) {
				numeroDocumentos++;
				byte[] baReturn = PDFGenerator.buildPDFExtrato(extrato, "Extrato Itau", paginasPorPDF, admin);
				FileOutputStream fs = new FileOutputStream(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
				fs.write(baReturn);
				fs.close();
				System.out.println(numeroDocumentos+"° exportado com sucesso!!");
				System.out.println(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
				extrato = cabecalho;
				linhaAtual = 0;
			}
		}
		if(!extrato.equalsIgnoreCase(cabecalho)) {
			numeroDocumentos++;
			byte[] baReturn = PDFGenerator.buildPDFExtrato(extrato, "Extrato Itau", paginasPorPDF, admin);
			FileOutputStream fs = new FileOutputStream(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
			fs.write(baReturn);
			fs.close();
			System.out.println(numeroDocumentos+"° exportado com sucesso!!");
			System.out.println(filePath + fileName + String.format("%03d", numeroDocumentos) + ".pdf");
		}
		System.out.println("Foi criado um total de: " + numeroDocumentos + " PDF(s)!");
	}
	
	private static boolean validadorLinha(String linha) {
		for (String tag : listarTagsDoCabecalho()) {
			if (linha.contains(tag)) {
				return true;
			}
		}
		return false;
	}

	private static String[] listarTagsDoCabecalho() {
		String[] listaTags = {"<TipoCliente>", "<Tipoconta>", "<CPFCNPJ>", "<TipoForm>",
				"<NConta>", "<Nome>", "<Agencia>", "<MesAno>", "<NroPag>", "<Statement>", "<Statement " };
		return listaTags;
	}
	
	private static Document buildXMLDocument(String xml) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (Exception e) {
			System.err.println("DocumentBuilderFactory: could not set parser feature");
		}
		DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
		Document xmlDoc = docBuilder.parse(new InputSource(new StringReader(xml)));

		return xmlDoc;
	}
}
