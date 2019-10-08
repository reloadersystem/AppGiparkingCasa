package com.giparking.appgiparking.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class GenerarPDF {

    private File pdFile;


    private void imprimirLote() throws IOException {
//    PrintWriter out = response.getWriter();

        String numeroLote = "121";//request.getParameter("numeroLote");
        int numeroFilas = 1;//Integer.parseInt(request.getParameter("numeroFilas"));
        String json = "";//request.getParameter("data");
        JSONArray JAResponse = new JSONArray();//json
//    out.println(numeroLote);
//    out.println(numeroFilas);
//    out.println(json);
        createPDF("121312", JAResponse);
    }

    private void createPDF(String numeroLote, JSONArray JAResponse) throws IOException {
        String fileName = "loteAdministrativo" + numeroLote + ".pdf";

        try {
            Document documento = new Document(PageSize.A4.rotate(), 5, 5, 5, 5);
            PdfWriter writer = PdfWriter.getInstance(documento,  new FileOutputStream(pdFile));
            MyFooter event = new MyFooter();
            writer.setPageEvent(event);
            documento.open();

            // logo saco oliveros y sistema helicoidal
            Image logoSOSH = Image.getInstance("C:\\AppServ\\www\\img\\so_sh.png");
            logoSOSH.scaleToFit(200, 200);
            logoSOSH.setAlignment(Chunk.ALIGN_LEFT);

            Image logoAPE = Image.getInstance("C:\\AppServ\\www\\img\\ap.png");
            logoAPE.scaleToFit(200, 200);
            logoAPE.setAlignment(Chunk.ALIGN_RIGHT);

            // tabla logo
            PdfPTable tablaLogo = new PdfPTable(2);
            tablaLogo.setWidthPercentage(100);
            PdfPCell celda1 = new PdfPCell();
            PdfPCell celda2 = new PdfPCell();
            celda1.setBorder(Rectangle.NO_BORDER);
            celda2.setBorder(Rectangle.NO_BORDER);
            celda1.addElement(logoSOSH);
            celda2.addElement(logoAPE);
            tablaLogo.addCell(celda1);
            tablaLogo.addCell(celda2);
            documento.add(tablaLogo);

            // titulo principal
            Paragraph titulo = new Paragraph("HABER ADMINISTRATIVO",
                    FontFactory.getFont("arial", 22, Font.UNDERLINE, BaseColor.BLACK));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            // lote
            Paragraph lote = new Paragraph("N° LOTE " + numeroLote,
                    FontFactory.getFont("arial", 22, Font.BOLD, BaseColor.BLACK));
            lote.setAlignment(Element.ALIGN_CENTER);
            documento.add(lote);

            // salto de linea
            documento.add(new Phrase(""));

            // cabecera
            String[] cabecera = {"NRO", "APELLIDOS Y NOMBRES", "TIPO DOC.", "NRO DOC.", "ESCALAFÓN", "PROPUESTO", "PRESIDENCIA", "OBSERVACIÓN"};
            PdfPTable tablaData = new PdfPTable(cabecera.length);
            tablaData.setWidths(new float[]{0.5f, 3, 1, 1, 1, 1, 1, 4});
            tablaData.setWidthPercentage(100);

            for (int i = 0; i < cabecera.length; i++) {
                switch (i) {
                    case 4:
                        tablaData.addCell(createCell("SUELDO (S/.)", 0.5f, 3, Element.ALIGN_CENTER, Font.BOLD));
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    default:
                        tablaData.addCell(createCell("", 0, 1, Element.ALIGN_CENTER, Font.BOLD));
                        break;
                }
            }

            for (String tituloCabecera : cabecera) {
                tablaData.addCell(createCell(tituloCabecera, 0.5f, 1, Element.ALIGN_CENTER, Font.BOLD));
            }


            documento.add(tablaData);

            // salto de linea
            documento.add(Chunk.NEWLINE);
            documento.add(Chunk.NEWLINE);
            documento.add(Chunk.NEWLINE);
            documento.add(Chunk.NEWLINE);
            documento.add(Chunk.NEWLINE);

            // responsables
            String[] responsables = new String[]{"Fabianna Janeht Gonzáles Saavedra", "Wilmer Carrasco Beas"};

            // cargos
            String[] cargos = new String[]{"Directora General", "Presidente de la A.C.E.S.O"};

            PdfPTable tablaAutorizacion = new PdfPTable(responsables.length);
            PdfPCell cellFirma = new PdfPCell(new Phrase("______________________________"));
            cellFirma.setBorder(Rectangle.NO_BORDER);

            PdfPCell cellResponsable = null;
            PdfPCell cellCargo = null;

            // firmas
            for (int i = 0; i < responsables.length; i++) {
                tablaAutorizacion.addCell(cellFirma);
            }

            // repsonsables
            for (int i = 0; i < responsables.length; i++) {
                cellResponsable = new PdfPCell(new Phrase(responsables[i]));
                cellResponsable.setBorder(Rectangle.NO_BORDER);
                tablaAutorizacion.addCell(cellResponsable);
            }

            // cargos
            for (int i = 0; i < responsables.length; i++) {
                cellCargo = new PdfPCell(new Phrase(cargos[i]));
                cellCargo.setBorder(Rectangle.NO_BORDER);
                tablaAutorizacion.addCell(cellCargo);
            }

            documento.add(tablaAutorizacion);

            documento.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    public PdfPCell createCell(String content, float borderWidth, int colspan, int alignment, int textStyle) {
        Font f = new Font(Font.FontFamily.HELVETICA, 7, textStyle, GrayColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(content, f));
        cell.setBorderWidth(borderWidth);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    class MyFooter extends PdfPageEventHelper {

        Font ffont = new Font(Font.FontFamily.COURIER, 5, Font.BOLD);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
//      String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

            PdfContentByte cb = writer.getDirectContent();
        }
    }

}
