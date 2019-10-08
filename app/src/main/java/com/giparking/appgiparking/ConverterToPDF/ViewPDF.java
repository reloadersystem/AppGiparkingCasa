package com.giparking.appgiparking.ConverterToPDF;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.giparking.appgiparking.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPDF extends AppCompatActivity {


    private PDFView pdfView;
    private File file;
    Button btnImprimir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = findViewById(R.id.pdfView);
        btnImprimir = findViewById(R.id.btnImprimir);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            file = new File(bundle.getString("path", ""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();


        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                {

                    //Environment.getExternalStorageDirectory() + "/PDFiles/", "Template.pdf"
                    //String printpdf = Environment.getExternalStorageDirectory() + "/PDFiles/Template.pdf";
                    PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getApplicationContext(), String.valueOf(file));
                    printManager.print("Historial", printAdapter, new PrintAttributes.Builder().build());
                }
            }
        });
    }
}
