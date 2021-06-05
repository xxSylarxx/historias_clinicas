package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.os.Environment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PdfHistoria {

    private Context context;
    private File pdfFile;
    private String nombre;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN,20, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.TIMES_ROMAN,18, Font.BOLD, BaseColor.DARK_GRAY);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN,12, Font.BOLD);
    private Font fHeader = new Font(Font.FontFamily.TIMES_ROMAN,15, Font.BOLD);

    public PdfHistoria(Context context, String nombre){
        this.context = context;
        this.nombre = nombre;
    }

    public void openDocument(){
        createFile();
        try{
            document = new com.itextpdf.text.Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createFile(){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),"PDF");

        if(!folder.exists()){
            folder.mkdirs();
        }

        String nombreArchivo = nombre+".pdf";
        pdfFile = new File(folder, nombreArchivo);
    }

    public void closeDocument(){
        document.close();
    }

    public void addMetaData(String title, String subject){
        document.addTitle(title);
        document.addSubject(subject);
    }

    public void addTitles(String title, String subtitle){
        try{
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subtitle, fSubtitle));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text){
        try{
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addParagraph2(String text){
        try{
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(1);
            paragraph.setSpacingBefore(1);
            document.add(paragraph);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void createTable2(int column,ArrayList<String[]> lista){
        try{
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(column);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;

            pdfPTable.getDefaultCell().setBorder(0);

            for(int indexR=0;indexR<lista.size();indexR++){
                if(indexR!=0 && indexR%12==0){
                    PdfPCell tmp = new PdfPCell(new Phrase("\n"));
                    tmp.setBorder(Rectangle.NO_BORDER);
                    tmp.setPadding(6);
                    pdfPTable.addCell(tmp);
                    pdfPTable.addCell(tmp);
                }

                String[] row = lista.get(indexR);
                for(int indexC=0;indexC<column;indexC++){
                    if(indexR%2==0){
                        pdfPCell = new PdfPCell(new Phrase(row[indexC],fHeader));
                        pdfPCell.setPaddingLeft(6);
                    }else{
                        pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                        pdfPCell.setPadding(6);
                    }

                    pdfPCell.setBackgroundColor(new BaseColor(255,255,255));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    pdfPCell.setBorder(Rectangle.NO_BORDER);

                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            document.add(paragraph);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public void createTable(String[] header, ArrayList<String[]> lista){
        try{
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            int indexC=0;
            while (indexC<header.length){
                pdfPCell = new PdfPCell(new Phrase(header[indexC++],fSubtitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GREEN);
                pdfPTable.addCell(pdfPCell);
            }

            for(int indexR=0;indexR<lista.size();indexR++){
                String[] row = lista.get(indexR);
                for(indexC=0;indexC<header.length;indexC++){
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            document.add(paragraph);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


}
