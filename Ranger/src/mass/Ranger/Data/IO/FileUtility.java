package mass.Ranger.Data.IO;

import android.content.Context;
import android.os.Environment;
import com.example.travinavi.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileUtility {
    private final static String TAG = FileUtility.class.getName();

    public static File getPhotoDirectory(Context context) {
        File folder = new File(getOrCreateDefaultDirectory(context.getString(R.string.app_name)), "photo");
        //noinspection ResultOfMethodCallIgnored
        mkdirs(folder);
        return folder;
    }

    public static File getOrCreateDefaultDirectory(String rootName) {
        File root = new File(Environment.getExternalStorageDirectory(), rootName);
        // Make sure the directory exists
        //noinspection ResultOfMethodCallIgnored
        mkdirs(root);
        return root;
    }

    public static boolean mkdirs(final File root) {
        return root.mkdirs();
    }

    public static long getFileSize(File file) {
        return file.length();
    }

    public static File getAudioDirectory(Context context) {
        File folder = new File(getOrCreateDefaultDirectory(context.getString(R.string.app_name)), "audio");
        //noinspection ResultOfMethodCallIgnored
        mkdirs(folder);
        return folder;
    }

    public static File[] getStacktraceFiles(Context context) {
        String rootName = context.getString(R.string.app_name);
        File root = FileUtility.getOrCreateDefaultDirectory(rootName);
        FilenameFilter stacktraceFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
                                                    fileName.length());
                return fileEnd.equalsIgnoreCase("stacktrace");
            }
        };
        return root.listFiles(stacktraceFilter);
    }

    public static ArrayList<String> getImageNames(File folderFile) {
        if (folderFile.isFile()) {
            //Logger.w(TAG, folderFile + " is not a folder", new Throwable());
            return new ArrayList<String>();
        }
        String[] filesList = folderFile.list();
        ArrayList<String> filesArrayList = new ArrayList<String>();
        for (String aFilesList : filesList) {
            File file = new File(folderFile, aFilesList);
            if (!file.isDirectory()) {
                if (isImageFile(file.getName())) {
                    filesArrayList.add(file.getName());
                }
            }
        }
        return filesArrayList;
    }

    private static boolean isImageFile(String fileName) {
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
                                            fileName.length());
        return fileEnd.equalsIgnoreCase("jpg") || fileEnd.equalsIgnoreCase("png") || fileEnd.equalsIgnoreCase("bmp");
    }

    public static ArrayList<String> getAudioNames(File folderFile) {
        if (folderFile.isFile()) {
            //Logger.w(TAG, folderFile + " is not a folder", new Throwable());
            return new ArrayList<String>();
        }
        String[] filesList = folderFile.list();
        ArrayList<String> filesArrayList = new ArrayList<String>();
        for (String aFilesList : filesList) {
            File file = new File(folderFile, aFilesList);
            if (!file.isDirectory()) {
                if (isAudioFile(file.getName())) {
                    filesArrayList.add(file.getName());
                }
            }
        }
        return filesArrayList;
    }

    private static boolean isAudioFile(String fileName) {
        String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1,
                                            fileName.length());
        return fileEnd.equalsIgnoreCase("aac");
    }

    public static boolean delete(String filePath) {
        if (filePath == null) {
            return false;
        }

        File temp = new File(filePath);
        return delete(temp);
    }

    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return deleteDirectory(file.getPath());
        } else {
            return deleteFile(file.getPath());
        }
    }

    private static boolean deleteDirectory(String filePath) {
        File f = new File(filePath);

        File[] files = f.listFiles();
        if (files == null) {
            return false;
        }

        boolean result = true;
        for (File file : files) {
            if (!delete(file)) {
                result = false;
            }
        }

        if (!f.delete()) {
            result = false;
        }

        return result;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        try {
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
}
