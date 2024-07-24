package com.yuruneji.cameratraining2.domain.usecase;

import timber.log.Timber;

/**
 * @author toru
 * @version 1.0
 */
public class HogeController {

    private HogeThread hogeThread;

    public HogeController() {
        Timber.i("HogeController");
        hogeThread = new HogeThread();
        hogeThread.start();
    }


    public void stop() {
        if (hogeThread != null) {
            hogeThread.interrupt();
            hogeThread = null;
        }
    }

    class HogeThread extends Thread {

        public HogeThread() {
            Timber.i("HogeThread");
        }

        @Override
        public synchronized void start() {
            super.start();
        }

        @Override
        public void run() {
            Timber.i("HogeThread.run() start");
            super.run();

            while (true) {
                try {
                    Thread.sleep(1000);

                    Timber.i("hoge");
                } catch (InterruptedException e) {
                    Timber.i("HogeThread interrupted");
                    break;
                }
            }

            Timber.i("HogeThread.run() end");
        }
    }

}
