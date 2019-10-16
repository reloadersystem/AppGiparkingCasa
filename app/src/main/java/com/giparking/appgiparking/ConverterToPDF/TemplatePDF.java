package com.giparking.appgiparking.ConverterToPDF;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PagerSnapHelper;
import android.util.Log;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.util.ConversionTitulo;
import com.giparking.appgiparking.util.GeneralFragmentManager;
import com.giparking.appgiparking.util.HoraFechaActual;
import com.giparking.appgiparking.util.str_global;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TemplatePDF {

    private Document document;

    private Context context;
    private File pdFile;
    private PdfWriter pdfWriter;

    Bitmap bitmapImg;
    ConversionTitulo conversionTitulo;

    private Paragraph paragraph;

    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 5, Font.NORMAL); //20 .BOLD
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN, 4, Font.NORMAL); //18
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 6, Font.NORMAL); //12
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 4, Font.NORMAL); //15, BaseColor.RED


    public TemplatePDF(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmapImg = bitmap;
    }

    public void openDocument() {
        createFile();
        try {

            //tamaño hoja programada
//            Rectangle pageSize = new Rectangle(200f, 400f); //ancho y alto
//            Document docu = new Document(pageSize);

            //margenes 72 =1 pulgada

            //document = new Document(PageSize.A8);//calibracion A8
            document = new Document(PageSize.A8,5,5,5,5);//calibracion A8

            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdFile));
            document.open();

            conversionTitulo = new ConversionTitulo();

            String nombreEmpresa = str_global.getInstance().getEmpresa_nombre();
            //String formatTituloEmpresa = conversionTitulo.obtenerTitulo(nombreEmpresa);
            Paragraph lote = new Paragraph(nombreEmpresa,
                    FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote.setAlignment(Element.ALIGN_CENTER);
            document.add(lote);


            String direccionEmpresa = str_global.getInstance().getSucursal_nombre();
            Paragraph lote2 = new Paragraph(direccionEmpresa,
                    FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote2.setAlignment(Element.ALIGN_CENTER);
            document.add(lote2);

            String lineSeparator = "_____________________________________________";
            Paragraph lote3 = new Paragraph(lineSeparator,
                    FontFactory.getFont("arial", 5, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote3.setAlignment(Element.ALIGN_CENTER);
            document.add(lote3);

            String ticketnum = "Ticket: "+str_global.getInstance().getCod_cefectivo(); //numTicket
            Paragraph lote4 = new Paragraph(ticketnum,
                    FontFactory.getFont("arial", 7, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote4.setAlignment(Element.ALIGN_CENTER);
            document.add(lote4);

            String numPlaca = "Placa: "+str_global.getInstance().getCod_usuario(); //numPlaca
            Paragraph lote5 = new Paragraph(numPlaca,
                    FontFactory.getFont("arial", 7, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote5.setAlignment(Element.ALIGN_CENTER);
            document.add(lote5);

            String fechaIngreso = "Fecha Ingreso: "+ HoraFechaActual.obtenerFecha(); //fechaIngreso
            Paragraph lote6 = new Paragraph(fechaIngreso,
                    FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote6.setAlignment(Element.ALIGN_CENTER);
            document.add(lote6);

            String horaIngreso = "Hora Ingreso: " + HoraFechaActual.obtenerHora(); //horaIngreso
            Paragraph lote7 = new Paragraph(horaIngreso,
                    FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote7.setAlignment(Element.ALIGN_CENTER);
            document.add(lote7);

            String file_path = Environment.getExternalStorageDirectory() + "/PDFiles/QRImage/qrParquer.png";
            File file = new File(file_path);
            FileInputStream fileInputStream = new FileInputStream(file);
            Bitmap bmp = BitmapFactory.decodeStream(fileInputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);//100
            Image image = Image.getInstance(stream.toByteArray());
            //image.setAbsolutePosition(10f, 750f);
//                image.scaleToFit(850, 78);
            image.scaleToFit(110, 110); //150, 150  | 80,80
            image.setAlignment(Element.ALIGN_CENTER | Element.ALIGN_CENTER);
            document.add(image);


            Paragraph lote8 = new Paragraph(lineSeparator,
                    FontFactory.getFont("arial", 5, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote8.setAlignment(Element.ALIGN_CENTER);
            document.add(lote8);

            String pieImpresion = "Gracias por preferirnos";
            Paragraph lote9 = new Paragraph(pieImpresion,
                    FontFactory.getFont("arial", 6, Font.NORMAL, BaseColor.BLACK)); //font.bold
            lote9.setAlignment(Element.ALIGN_CENTER);
            document.add(lote9);




        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/PDFiles/Template/");

        if (!folder.exists()) {
            folder.mkdirs();
            pdFile = new File(folder, "Template.pdf");
        } else {
            pdFile = new File(folder, "Template.pdf");
        }
    }

    public void closeDocument() {
        document.close();
    }

    public void addMetadata(String title, String subject, String autor) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(autor);
    }

    public void addTitles(String title, String subTitle, String date) {

        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subTitle, fSubtitle));
            addChildP(new Paragraph("Generado: " + date, fHighText));

            paragraph.setSpacingAfter(1);//30
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTitles", e.toString());
        }

    }

    private void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {

        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(1);//5
            paragraph.setSpacingBefore(1);//5
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("addParagraph", e.toString());
        }
    }


    public void viewPDF(Activity activity, Fragment fragment) {

        Bundle args = new Bundle();
        args.putString("path", pdFile.getAbsolutePath());

        GeneralFragmentManager.setFragmentWithReplace(activity, R.id.contenedor, fragment, args);
    }
}
