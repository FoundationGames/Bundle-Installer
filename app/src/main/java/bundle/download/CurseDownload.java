package bundle.download;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;

import java.nio.file.Path;

public class CurseDownload extends AbstractDownload {
    public final int projectID, fileID;

    public CurseDownload(String name, int projectID, int fileID) {
        super(name);
        this.projectID = projectID;
        this.fileID = fileID;
    }

    @Override
    public void downloadTo(Path path) throws DownloadException {
        try {
            CurseAPI.downloadFileToDirectory(projectID, fileID, path);
        } catch (CurseException e) { dlEx(e); }
    }
}
