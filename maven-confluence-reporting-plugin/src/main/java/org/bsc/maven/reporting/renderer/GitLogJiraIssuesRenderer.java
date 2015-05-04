package org.bsc.maven.reporting.renderer;

import com.github.danielflower.mavenplugins.gitlog.GitLogHelper;
import com.github.danielflower.mavenplugins.gitlog.SinceVersionRule;
import com.github.danielflower.mavenplugins.gitlog.VersionUtil;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author ar
 * @since Date: 01.05.2015
 */
public class GitLogJiraIssuesRenderer extends AbstractMavenReportRenderer {

    private final Log log;
    private String gitLogSinceTagName;
    private SinceVersionRule sinceVersionRule;
    private String currentVersion;

    /**
     * Default constructor.
     *
     * @param sink the sink to use.
     */
    public GitLogJiraIssuesRenderer(Sink sink, String gitLogSinceTagName,  String currentVersion, SinceVersionRule sinceVersionRule, Log log) {
        super(sink);
        this.gitLogSinceTagName = gitLogSinceTagName;
        this.currentVersion = currentVersion;
        this.sinceVersionRule = sinceVersionRule;
        this.log = log;
    }

    @Override
    public String getTitle() {
        return "GitLogJiraIssuesRendererTitle";  //todo implement getTitle in GitLogJiraIssuesRenderer
    }

    @Override
    protected void renderBody() {


        //    startSection( getTitle() );

        GitLogHelper gitLogHelper = new GitLogHelper(log);
        try {
            gitLogHelper.openRepository();
        } catch (Exception e) {
            log.warn("cannot open git repository " , e);
        }

        Date sinceDate = new Date(0L);
        try {

            if (!SinceVersionRule.SINCE_BEGINNING.equals(sinceVersionRule)) {
                String tagNamePart = VersionUtil.calculateSinceVersionTagNamePart(currentVersion, sinceVersionRule);
                Collection<String> tagNames = gitLogHelper.getTagNames();
                List<String> tagNamesOfVersions = VersionUtil.calculateTagNamesOfVersions(tagNames, currentVersion, sinceVersionRule);

                for (String tagNameWithVersion : tagNamesOfVersions){
                    Date date = gitLogHelper.extractDateOfCommitWithTagName(tagNameWithVersion);
                    if (date.after(sinceDate)){
                        sinceDate = date;
                    }
                }


            }  else {
                sinceDate = gitLogHelper.extractDateOfCommitWithTagName(gitLogSinceTagName);
            }
        } catch (IOException e) {
            log.warn("cannot extract date of commit with tag name " , e);
        }

        String report = gitLogHelper.generateIssuesReport(sinceDate);

        sink.rawText(report);

        //    endSection();

            return;


    }

//    public void render()
//    {
//
//
//        sink.body();
//        renderBody();
//        sink.body_();
//
//        sink.flush();
//
//        sink.close();
//    }
}
