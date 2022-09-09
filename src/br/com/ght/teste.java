package br.com.ght;
import br.com.ght.OutputHandler;

public class teste {

	public static void main(String[] args) throws Exception {
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\agora.xml";
		//String szPDFSaida = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\testando.pdf";
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\oitomil.txt";
		
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\xmlPoupanca2.xml";
		String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\oitomil2.txt";
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\paulofraga.xml";
		
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\aguaview.txt";
		
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\texto2022.txt";
		String szPDFSaida = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\2022\\";
		boolean bVisaoAdm = false;
		int iQtdePags = 500;
		
		long start = System.currentTimeMillis();
		OutputHandler.getExtratoPDF(szXML, szPDFSaida, bVisaoAdm, iQtdePags);
		long end = System.currentTimeMillis();
		
		System.out.println("Demorou: " + (end-start));

	}
}