package com.mispower.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * data process
 *
 * @author wuguolin
 */
public class DataProcess implements Runnable {

    private Event<File> event;

    DataProcess(Event<File> event) {
        this.event = event;
    }

    @Override
    public void run() {
        if (event != null && event.getData().size() > 0) {
            process();
        }
    }

    /**
     * process data logic
     */
    private void process() {
        FileAdaptor fileAdaptor;
        List<File> files = event.getData();
        for (File file : files) {
            fileAdaptor = new FileAdaptor.Builder().setFile(file).build();
            /*
             * todo
             */
            /**
             * close file reader buffer
             */
            try {
                fileAdaptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean success = false;
            switch (event.getPolicy()) {
                case CHANGE:
                    success = file.renameTo(new File(String.format("%s.%s", file.getAbsoluteFile(), event.getCompleted())));
                    break;
                case DELETE:
                    success = file.delete();
                    break;
                default:
                    break;
            }
            if (success) {
                synchronized (event.getActiveFiles()) {
                    event.getActiveFiles().remove(file.getName());
                }
            }
        }
    }


}