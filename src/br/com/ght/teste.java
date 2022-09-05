package br.com.ght;
import br.com.ght.OutputHandler;

public class teste {

	public static void main(String[] args) throws Exception {
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\agora.xml";
		//String szPDFSaida = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\testando.pdf";
		String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\aguaview.txt";
		
		//String szXML = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\texto2022.txt";
		String szPDFSaida = "C:\\Users\\weslley.guedes\\Desktop\\PDFConverter\\2022\\";
		boolean bVisaoAdm = true;
		int iQtdePags = 200;
				
		OutputHandler.getExtratoPDF(szXML, szPDFSaida, bVisaoAdm, iQtdePags);

	}
}