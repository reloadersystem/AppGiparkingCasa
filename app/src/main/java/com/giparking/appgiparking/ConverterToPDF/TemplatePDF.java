package com.giparking.appgiparking.ConverterToPDF;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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

    private Paragraph paragraph;

    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);


    public TemplatePDF(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmapImg = bitmap;
    }

    public void openDocument() {
        createFile();
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdFile));
            document.open();

            Paragraph lote = new Paragraph("N° LOTE " + "222",
                    FontFactory.getFont("arial", 22, Font.BOLD, BaseColor.BLACK));
            lote.setAlignment(Element.ALIGN_CENTER);
            document.add(lote);

            String file_path = Environment.getExternalStorageDirectory() + "/PDFiles/QRImage/qrParquer.png";

            File file = new File(file_path);
            FileInputStream fileInputStream = new FileInputStream(file);
 Bitmap bmp = BitmapFactory.decodeStream(fileInputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            //image.setAbsolutePosition(10f, 750f);
//                image.scaleToFit(850, 78);
            image.scaleToFit(150, 150);
            image.setAlignment(Element.ALIGN_CENTER | Element.ALIGN_CENTER);
            document.add(image);

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

            paragraph.setSpacingAfter(30);
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
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("addParagraph", e.toString());
        }
    }


    public void viewPDF() {
        Intent intent = new Intent(context, ViewPDF.class);
        intent.putExtra("path", pdFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
