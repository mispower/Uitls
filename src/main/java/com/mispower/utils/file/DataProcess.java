package com.mispower.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Data process. If you want do your business,just overwrite <code>process()</code>
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
            List<File> files = event.getData();
            for (File file : files) {
                try {
                    process(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doPolicy(file);
            }
        }
    }

    /**
     * process data logic
     */
    public void process(File file) throws IOException {
        final FileAdaptor fileAdaptor = new FileAdaptor.Builder().setFile(file).build();
        /*
         * todo
         */
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = fileAdaptor.read(buffer)) != -1) {
            System.out.println(new String(buffer, 0, len));
        }

        //release memory
        try {
            fileAdaptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * policy operation
     *
     * @param file file
     */
    private void doPolicy(File file) {
        boolean success = false;
        switch (event.getPolicy()) {
            case CHANGE:
                success = file.renameTo(new File(String.format("%s.%s", file.getAbsoluteFile(),
                        event.getCompleted())));
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