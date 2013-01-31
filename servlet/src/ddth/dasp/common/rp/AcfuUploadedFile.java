package ddth.dasp.common.rp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

public class AcfuUploadedFile implements IUploadedFile {

    private FileItem fileItem;

    public AcfuUploadedFile(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return fileItem.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileContentType() {
        return fileItem.getContentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFileSize() {
        return fileItem.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getFileContent() {
        return fileItem.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getFileInputStream() throws IOException {
        return fileItem.getInputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCachedInMemory() {
        return fileItem.isInMemory();
    }
}
