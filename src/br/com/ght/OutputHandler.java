package br.com.ght;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @param xmlPath Diretorio completo do arquivo XML
	 * @param pdfSaida Diretorio do local de armazenamento do PDF + nome dos arquivos PDF gerados
	 * @param admin Utilizado apenas na gaveta 'Extrato Itau'. Valores aceitos: true = administrador | false = cliente
	 * @param paginasPorPDF Número de páginas para cada arquivo PDF 
	 **/
	public static void getExtratoPDF(String xmlPath, String pdfSaida, boolean admin, int paginasPorPDF) throws Exception {
		String fileName = "";
		String filePath = "";
		
		if(pdfSaida.length() < 1) {
			System.err.println("Endereço do PDF(s) vazio!");
			return;
		}
		if(xmlPath.length() < 1) {
			System.out.println("Endereço XML vazio!");
			return;
		}
		
		//new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		if(!(pdfSaida.toLowerCase().startsWith("c:") || pdfSaida.toLowerCase().startsWith("d:"))) {
			filePath = new File(OutputHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			filePath = filePath.substring(0, filePath.length() - 16);
			
			if(pdfSaida.toLowerCase().endsWith(".pdf")) {
				fileName = pdfSaida.substring(pdfSaida.lastIndexOf("\\") + 1, pdfSaida.length() - 4);
			}else
				fileName = xmlPath.substring(xmlPath.lastIndexOf("\\") + 1,xmlPath.length() - 4);
		}
		else {
			if(pdfSaida.toLowerCase().endsWith(".pdf")) {
				filePath = pdfSaida.substring(0, pdfSaida.lastIndexOf("\\") + 1);
				fileName = pdfSaida.substring(pdfSaida.lastIndexOf("\\") + 1, pdfSaida.length() - 4);
			}else {
				if(!pdfSaida.substring(pdfSaida.length() - 1).equals("\\"))
					filePath = pdfSaida + "\\";
				else
					filePath = pdfSaida;
				fileName = xmlPath.substring(xmlPath.lastIndexOf("\\") + 1,xmlPath.length() - 4);
			}
		}
		
		System.out.println("Nome do arquivo: "+ fileName);
		System.out.println("Salvar em: "+ filePath);
		
		Scanner scanner = new Scanner(new FileReader(xmlPath));
		int linhaPorPagina;
		
		if(admin)
			linhaPorPagina = 39;
		else
			linhaPorPagina = 55;
		
		int totalDocLinhas = linhaPorPagina*paginasPorPDF;
		
		String cabecalho = makeHeader(xmlPath);
		int linhaAtual = 0 , numeroDocumentos = 0;
		
		String extrato = cabecalho;
		
		Matcher match = Pattern.compile("([0-3][0-9]/[0-3][0-9].*)").matcher(new String(Files.readAllBytes(Paths.get(xmlPath))));

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
	
	private static String makeHeader(String extrato) throws FileNotFoundException {
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
}
