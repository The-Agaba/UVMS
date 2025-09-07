package com.example.uvms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.OutputStream;

public class Pdf_Util {

    // Request code to handle result in Activity
    public static final int REQUEST_CODE_CREATE_PDF = 101;

    /**
     * Opens a "Save As" dialog for the user to choose PDF location.
     * The calling Activity must handle onActivityResult.
     */
    public static void promptSavePdf(Activity activity, String suggestedFileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedFileName);
        activity.startActivityForResult(intent, REQUEST_CODE_CREATE_PDF);
    }

    /**
     * Call this after the user picks a location from SAF to actually save the PDF.
     *
     * @param context  Any context
     * @param view     The view to render as PDF
     * @param uri      The Uri returned by SAF
     */
    public static void saveViewToUri(Context context, View view, Uri uri) {
        // A4 page size
        int pageWidth = 595;
        int pageHeight = 842;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        // Measure and scale view to fit PDF page
        float scale = Math.min((float) pageWidth / view.getWidth(), (float) pageHeight / view.getHeight());
        canvas.save();
        canvas.scale(scale, scale);
        view.draw(canvas);
        canvas.restore();

        document.finishPage(page);

        // Write PDF to Uri
        try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
            if (out != null) {
                document.writeTo(out);
                Log.d("PdfUtil", "PDF successfully saved to: " + uri.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PdfUtil", "Error saving PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}
