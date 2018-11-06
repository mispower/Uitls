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

    private List<File> files;

    DataProcess(List files) {
        this.files = files;
    }

    @Override
    public void run() {
        if (files != null && files.size() > 0) {
            doWork();
        }
    }

    private void doWork() {
        FileAdaptor fileAdaptor;
        for (File file : files) {
            fileAdaptor = new FileAdaptor.Builder().setFile(file).build();
            /*
             * todo
             */
            try {
                fileAdaptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (PolicyEnum.CHANGE) {
                case CHANGE:
                    file.renameTo(new File(file.getAbsoluteFile() + "." + "completed"));
                    break;
                case DELETE:
                    file.delete();
                    break;
                default:
                    break;
            }
        }
    }


}