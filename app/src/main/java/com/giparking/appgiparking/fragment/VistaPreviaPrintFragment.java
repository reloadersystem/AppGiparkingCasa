package com.giparking.appgiparking.fragment;


import android.content.Context;
import android.os.Bundle;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.giparking.appgiparking.ConverterToPDF.PdfDocumentAdapter;
import com.giparking.appgiparking.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class VistaPreviaPrintFragment extends Fragment {

    private PDFView pdfView;
    private File file;
    Button btnImprimir;

    View  rootview;

    public VistaPreviaPrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_vista_previa_print, container, false);

        pdfView = rootview.findViewById(R.id.pdfView);
        btnImprimir = rootview.findViewById(R.id.btnImprimir);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            file = new File(bundle.getString("path", ""));

        }

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            file = new File(bundle.getString("path", ""));
//        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();


        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
                {

                    //Environment.getExternalStorageDirectory() + "/PDFiles/", "Template.pdf"
                    //String printpdf = Environment.getExternalStorageDirectory() + "/PDFiles/Template.pdf";
                    PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getContext(), String.valueOf(file));
                    printManager.print("Historial", printAdapter, new PrintAttributes.Builder().build());
                }
            }
        });

        return rootview;
    }

}
