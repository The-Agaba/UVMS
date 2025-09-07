package com.example.uvms;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPrintAdapter extends PrintDocumentAdapter {

    private final Context context;
    private final View view;
    private PrintedPdfDocument pdfDocument;

    public ViewPrintAdapter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, android.os.Bundle extras) {

        // Cancel if needed
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        // Create PDF document with proper attributes
        pdfDocument = new PrintedPdfDocument(context, newAttributes);

        // Prepare document info
        PrintDocumentInfo info = new PrintDocumentInfo
                .Builder("license.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build();

        // Layout finished
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(android.print.PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            return;
        }

        if (pdfDocument == null) {
            callback.onWriteFailed("PDF Document not initialized");
            return;
        }

        // Start page
        PdfDocument.Page page = pdfDocument.startPage(0);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY)
        );
        view.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
        view.draw(page.getCanvas());
        pdfDocument.finishPage(page);

        // Write PDF to destination
        try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(out);
            callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
        } catch (IOException e) {
            e.printStackTrace();
            callback.onWriteFailed(e.getMessage());
        } finally {
            pdfDocument.close();
        }
    }
}
