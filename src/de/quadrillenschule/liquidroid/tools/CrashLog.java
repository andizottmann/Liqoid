package de.quadrillenschule.liquidroid.tools;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;


// Usage in onCreate via  Thread.setDefaultUncaughtExceptionHandler(new CrashLog(new File(getExternalFilesDir(null), "liqoid.log")));
 
public class CrashLog implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;

    private File file;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CrashLog(File file) {
       this.file=file;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        String timestamp = System.currentTimeMillis()+"";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

      writeToFile(stacktrace);

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(file));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
}