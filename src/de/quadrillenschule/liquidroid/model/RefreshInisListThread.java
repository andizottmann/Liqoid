/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.model;

import android.os.Handler;
import de.quadrillenschule.liquidroid.LiqoidApplication;
import java.util.ArrayList;

/**
 *
 * @author andi
 */
public class RefreshInisListThread extends Thread {

    boolean download;
    private MultiInstanceInitiativen allInititiativen;
    Handler progressHandler;
    LiqoidApplication app;
    public LQFBInstance currentInstance;
    public String currentlyDownloadedInstance = "", currentlyDownloadedArea = "";
    public long overallDataAge = 0;
    public boolean dataComplete = true;
    public static int FINISH_OK = 0, DOWNLOADING = 1, DOWNLOAD_ERROR = -1, DOWNLOAD_RETRY = 2, UPDATING = 4, DOWNLOADING_INSTANCE = 5, READ_CACHE = 6;
    ArrayList<RefreshInisListListener> refreshInisListListeners;
    public static boolean running = false;

    public RefreshInisListThread(boolean download, ArrayList<RefreshInisListListener> refreshInisListListeners, Handler progressHandler, LiqoidApplication app) {
        this.refreshInisListListeners = refreshInisListListeners;
        this.download = download;
        this.app = app;
        this.progressHandler = progressHandler;
        allInititiativen = new MultiInstanceInitiativen();
        if (download) {
            for (LQFBInstance myInstance : app.lqfbInstances) {
                myInstance.pauseDownload = false;
            }
        }
    }

    void updateAreas() {
        for (LQFBInstance myInstance : app.lqfbInstances) {
            currentInstance = myInstance;
            boolean doesDownload = false;
            if (myInstance.willDownloadAreas(app.cachedAPI1Queries, download, myInstance.getApiversion())) {
                currentlyDownloadedInstance = myInstance.getShortName();
                progressHandler.sendEmptyMessage(DOWNLOADING_INSTANCE);
                doesDownload = true;
            }
            int retrycounter = 0;
            int maxretries = 1;
            boolean instancedownload = download;
            if (myInstance.pauseDownload) {
                maxretries = 0;
                instancedownload = false;
            }

            while ((retrycounter <= maxretries) && (myInstance.downloadAreas(app.cachedAPI1Queries, instancedownload, myInstance.pauseDownload)) < 0) {

                if (doesDownload) {
                    progressHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                }
                try {
                    this.sleep((2 ^ retrycounter) * 1000);
                    retrycounter++;
                } catch (InterruptedException ex) {
                }
                if (doesDownload) {
                    progressHandler.sendEmptyMessage(DOWNLOAD_RETRY);
                } else {
                    progressHandler.sendEmptyMessage(READ_CACHE);
                }
            }
            if (retrycounter >= maxretries) {
                myInstance.pauseDownload = true;

            }
        }
    }

    @Override
    public void run() {

        if (running) {
            return;
        }
        running = true;
        progressHandler.sendEmptyMessage(UPDATING);
        dataComplete = true;
        updateAreas();
        overallDataAge = System.currentTimeMillis();

        for (LQFBInstance myInstance : app.lqfbInstances) {
            currentlyDownloadedInstance = myInstance.getShortName();

            for (Area a : myInstance.areas.getSelectedAreas()) {
                currentlyDownloadedArea = a.getName();
                boolean doesDownload = false;
                if (myInstance.willDownloadInitiativen(a, app.cachedAPI1Queries, download, myInstance.getApiversion())) {
                    progressHandler.sendEmptyMessage(DOWNLOADING);
                    doesDownload = true;
                }
                if (doesDownload) {
                    progressHandler.sendEmptyMessage(DOWNLOAD_RETRY);
                } else {
                    progressHandler.sendEmptyMessage(READ_CACHE);
                }
                int retrycounter = 0;
                int maxretries = 4;
                boolean instancedownload = download;
                if (myInstance.pauseDownload) {
                    maxretries = 0;
                    instancedownload = false;
                }

                while ((retrycounter <= maxretries) && (myInstance.downloadInitiativen(a, app.cachedAPI1Queries, instancedownload, myInstance.pauseDownload) < 0)) {
                    if (doesDownload) {
                        progressHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                    }
                    try {
                        this.sleep((2 ^ retrycounter) * 1000);
                        retrycounter++;

                    } catch (InterruptedException ex) {
                    }
                    if (doesDownload) {
                        progressHandler.sendEmptyMessage(DOWNLOAD_RETRY);
                    } else {
                        progressHandler.sendEmptyMessage(READ_CACHE);
                    }

                }
                if (retrycounter >= maxretries) {
                    if (!myInstance.pauseDownload) {
                        dataComplete = false;
                    }
                    myInstance.pauseDownload = true;

                } else {
                    myInstance.pauseDownload = false;
                }
                if (overallDataAge > app.cachedAPI1Queries.dataage) {
                    overallDataAge = app.cachedAPI1Queries.dataage;
                }
            }
            for (Area a : myInstance.areas.getSelectedAreas()) {
                for (Initiative i : a.getInitiativen()) {
                    allInititiativen.add(i);
                }
            }
        }
        for (RefreshInisListListener r : refreshInisListListeners) {
            r.finishedRefreshInisList((MultiInstanceInitiativen) allInititiativen.clone());
        }
        progressHandler.sendEmptyMessage(FINISH_OK);
        running = false;
    }

    public interface RefreshInisListListener {

        public void finishedRefreshInisList(MultiInstanceInitiativen newInis);
    }
}
