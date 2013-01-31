package ddth.dasp.common.mls;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * 
 * This language pack loads language elements from files on disks.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class FilePropsBasedLanguage extends PropsBasedLanguage {

    private String languageDir;

    public FilePropsBasedLanguage() {
    }

    public FilePropsBasedLanguage(Locale locale, String name) {
        super(locale, name);
    }

    public String getLanguageDir() {
        return languageDir;
    }

    public void setLanguageDir(String languageDir) {
        this.languageDir = languageDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        File dir = new File(languageDir);
        if (!dir.isDirectory()) {
            String msg = "[" + dir.getAbsolutePath() + "] is not a directory or not accessible!";
            throw new RuntimeException(msg);
        }

        for (File file : dir.listFiles()) {
            if (file.isFile() && file.canRead()) {
                String fileName = file.getName();
                if (fileName.endsWith(".properties") || fileName.endsWith(".xml")) {
                    Properties props = new Properties();
                    FileInputStream fis = FileUtils.openInputStream(file);
                    try {
                        if (fileName.endsWith(".properties")) {
                            props.load(fis);
                        } else {
                            props.loadFromXML(fis);
                        }
                        addLanguage(props);
                    } finally {
                        IOUtils.closeQuietly(fis);
                    }
                }
            }
        }
    }

    public void destroy() {
        // EMPTY
    }
}
