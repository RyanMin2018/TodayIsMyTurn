package kr.co.sology.fw.util;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;

import java.util.Objects;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

public class PrintUtil {

    public static void printWebPage(MainActivity mainActivity, WebView webView) {
        PrintManager printManager = (PrintManager) mainActivity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter();
        String job = mainActivity.getString(R.string.app_name);
        Objects.requireNonNull(printManager).print(job, printDocumentAdapter, new PrintAttributes.Builder().build());
    }



}
