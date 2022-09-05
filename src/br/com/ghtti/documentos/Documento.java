package br.com.ghtti.documentos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Weslley Sousa
 * @version 1.0
 * @build: 01/09/2020
 * 
 *         Classe responsável por formatar a saída 'RESULT_LINE' do DXServer nos
 *         formatos PDF, CSV e JSON.
 * 
 */
public class Documento {
	public Map<String, String> campos = new HashMap<String, String>();

	/**
	 * Método responsável por gerar documento(s) PDF a partir da(s) linha(s)
	 * exportada(s) do DXServer
	 * <p>
	 * 
	 * @param lines              Linhas exportadas pelo DXServer no formato
	 *                           'RESULT_LINE'
	 * @param regExpTemplaytList Lista de expressão regular
	 * @param layoutID           ID do layout a ser aplicado
	 * @param pdfTemplateFile    Caminho completo para o arquivo de template PDF
	 * @param base64             Converte o array de byte em base64
	 * @return Documento(s) em formato PDF
	 */
	public static byte[] parseLinePDF(String lines, Map<String, String> regExpTemplaytList, String layoutID,
			String pdfTemplateFile, boolean base64) {
		String[] arrLines = lines.split("\\r?\\n");
		ArrayList<Documento> dList = new ArrayList<Documento>();
		String regExp = "";
		Documento documento = null;

		for (String line : arrLines) {
			documento = new Documento();
			if (!regExpTemplaytList.containsKey(layoutID)) {
				System.out.println(
						"Documento.parseLinesPDF() > Expressao regular nao defnida para o Layout ID: " + layoutID);
				return null;
			}
			regExp = regExpTemplaytList.get(layoutID);

			if (regExp.isEmpty()) {
				System.out.println(
						"Documento.parseLinesPDF() > Expressao regular nao definida para Layout ID '" + layoutID + "'");
				return null;
			}

			Set<String> namedGroups = getNamedGroupLine(regExp);
			Pattern pattern = Pattern.compile(regExp);
			Matcher matcher = pattern.matcher(line);

			if (matcher.find()) {
				// Remove invalid groups
				Iterator<String> i = namedGroups.iterator();
				while (i.hasNext()) {
					try {
						matcher.group(i.next());
					} catch (IllegalArgumentException e) {
						i.remove();
					}
				}
				documento.campos = printMatches(matcher, namedGroups);
			}
			dList.add(documento);
		}
		return PDFGenerator.createPDF(dList, pdfTemplateFile, base64);
	}

	/**
	 * Método responsável por gerar documento CSV a partir da(s) linha(s)
	 * exportada(s) do DXServer
	 * <p>
	 * 
	 * @param lines              Linhas exportadas pelo DXServer no formato
	 *                           'RESULT_LINE'
	 * @param regExpTemplaytList Lista de expressão regular
	 * @param layoutID           ID do layout a ser aplicado
	 * @param bCreateHeader      Adiciona o nome dos campos na primeira linha
	 * @param base64             Converte o array de byte em base64
	 * @return Documento em formato CSV
	 */
	public static byte[] parseLineCSV(String lines, Map<String, String> regExpTemplaytList, String layoutID,
			boolean bCreateHeader, boolean base64) {
		String[] arrLines = lines.split("\\r?\\n");
		StringBuilder csv = new StringBuilder();
		String regExp = "";
		Documento documento = null;

		if (!regExpTemplaytList.containsKey(layoutID)) {
			System.out.println(
					"Documento.ParseLinesCSV() > Expressao regular nao definida para Layout ID '" + layoutID + "'");
			return null;
		}

		regExp = regExpTemplaytList.get(layoutID);

		if (regExp.isEmpty()) {
			System.out.println(
					"Documento.ParseLinesCSV() > Expressao regular nao definida para Layout ID '" + layoutID + "'");
			return null;
		}

		for (String line : arrLines) {
			Set<String> namedGroups = getNamedGroupLine(regExp);
			Pattern pattern = Pattern.compile(regExp);
			Matcher matcher = pattern.matcher(line);

			if (matcher.find()) {
				documento = new Documento();
				// Remove invalid groups
				Iterator<String> i = namedGroups.iterator();
				while (i.hasNext()) {
					try {
						matcher.group(i.next());
					} catch (IllegalArgumentException e) {
						i.remove();
					}
				}
				documento.campos = printMatches(matcher, namedGroups);
			}

			if (documento.campos.entrySet() == null) {
				System.err.println("Documento.parseLineCSV() -> could not set Campos");
				return null;
			}

			if (bCreateHeader) {
				for (String cabecalho : namedGroups) {
					csv.append(cabecalho + ",");
				}
				csv.setLength(csv.length() - 1);
				csv.append("\r\n");
				bCreateHeader = false;
			}

			for (Entry<String, String> entry : documento.campos.entrySet()) {
				csv.append(entry.getValue().trim() + ",");
			}

			csv.setLength(csv.length() - 1);
			csv.append("\r\n");
		}

		if (base64)
			return java.util.Base64.getEncoder().encode(csv.toString().getBytes());
		else
			return csv.toString().getBytes();
	}

	/**
	 * Método responsável por gerar documento JSON a partir da(s) linha(s)
	 * exportada(s) do DXServer
	 * <p>
	 * 
	 * @param lines              Linhas exportadas pelo DXServer no formato
	 *                           'RESULT_LINE'
	 * @param regExpTemplaytList Lista de expressão regular
	 * @param layoutID           ID do layout a ser aplicado
	 * @param base64             Converte o array de byte em base64
	 * @return Documento em formato JSON
	 */
	public static byte[] parseLineJSON(String lines, Map<String, String> regExpTemplaytList, String layoutID,
			boolean base64) {
		String[] arrLines = lines.split("\\r?\\n");
		boolean bFirst = true;
		Documento documento = null;
		if (!regExpTemplaytList.containsKey(layoutID)) {
			System.out
					.println("Documento.parseLineJSON() > Expressao regular nao defnida para o Layout ID: " + layoutID);
			return null;
		}

		String regExp = regExpTemplaytList.get(layoutID);

		if (regExp.isEmpty()) {
			System.out.println(
					"Documento.parseLineJSON() > Expressao regular nao definida para Layout ID '" + layoutID + "'");
			return null;
		}

		Set<String> namedGroups = getNamedGroupLine(regExp);
		StringBuilder sbJSON = new StringBuilder("{\"Result\": [ {");
		Pattern pattern = Pattern.compile(regExp);

		for (String line : arrLines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				documento = new Documento();
				// Remove invalid groups
				Iterator<String> i = namedGroups.iterator();
				while (i.hasNext()) {
					try {
						matcher.group(i.next());
					} catch (IllegalArgumentException e) {
						i.remove();
					}
				}
				documento.campos = printMatches(matcher, namedGroups);
			}
			if (documento.campos.entrySet() == null) {
				System.err.println("Documento.parseLineJSON() -> could not set Campos");
				return null;
			}
			if (!bFirst) {
				sbJSON.replace(sbJSON.length() - 1, sbJSON.length(), "},{");
			}

			for (Entry<String, String> entry : documento.campos.entrySet()) {
				sbJSON.append("\"").append(entry.getKey()).append("\": ").append(" \"").append(entry.getValue().trim())
						.append("\",");
				continue;
			}
			bFirst = false;
		}

		sbJSON.delete(sbJSON.length() - 1, sbJSON.length());
		sbJSON.append("}]}");

		if (base64)
			return java.util.Base64.getEncoder().encode(sbJSON.toString().getBytes());
		else
			return sbJSON.toString().getBytes();
	}

	private static Set<String> getNamedGroupLine(String regex) {
		Set<String> namedGroups = new LinkedHashSet<String>();
		Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);
		while (m.find()) {
			namedGroups.add(m.group(1));
		}
		return namedGroups;
	}

	private static Map<String, String> printMatches(Matcher matcher, Set<String> namedGroups) {
		Map<String, String> campos = new LinkedHashMap<String, String>();
		for (String name : namedGroups) {
			String matchedString = matcher.group(name);
			if (matchedString != null) {
				campos.put(name, matchedString);
			}
		}
		return campos;
	}
}