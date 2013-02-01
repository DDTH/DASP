package ddth.dasp.common.rp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IUploadedFile {

    public File getTempFile();

    public String getFileName();

    public String getFileContentType();

    public long getFileSize();

    /**
     * Gets the whole file content as a byte[].
     * 
     * Note: should only be used if file size is small and/or
     * {@link #isCachedInMemory()} returns true.
     * 
     * @return
     */
    public byte[] getFileContent();

    /**
     * Gets file content as an {@link InputStream}.
     * 
     * @return
     * @throws IOException
     */
    public InputStream getFileInputStream() throws IOException;

    public boolean isCachedInMemory();
}
