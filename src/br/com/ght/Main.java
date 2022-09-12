package br.com.ght;

public class Main {

	public static void main(String[] args) throws Exception {

		if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
			System.out.println("Uso:");
			System.out.println("java -cp PDFConverter.jar;forms-7.2.2.jar;io-7.2.2.jar;kernel-7.2.2.jar;layout-7.2.2.jar;slf4j-api-1.7.25.jar;slf4j-jdk14-1.7.25.jar;commons-7.2.2.jar br.com.ght.Main <arquivo_entrada> <arquivo_pdf> <visao_adm> <quantidade_pags_por_pdf>, onde <visao_adm> = true|false");
			return;
		}
		
		String szXML = args[0];
		String szPDFSaida = args[1];
		boolean bVisaoAdm = Boolean.parseBoolean(args[2]);
		int iQtdePags = Integer.parseInt(args[3]);
				
		OutputHandler.getExtratoPDF(szXML, szPDFSaida, bVisaoAdm, iQtdePags);		
	}
}
