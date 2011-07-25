package br.com.ingenieux.mojo.beanstalk;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsResult;

/**
 * Deletes application versions, either by count and/or by date old
 * 
 * @goal clean-previous-versions
 * 
 * @author Aldrin Leal
 */
public class CleanPreviousVersionsMojo extends AbstractBeanstalkMojo {
	/**
	 * Delete the source bundle?
	 * 
	 * @parameter expr="${deleteSourceBundle}"
	 */
	boolean deleteSourceBundle = false;

	/**
	 * How many versions to keep?
	 * 
	 * @parameter default-value="${beanstalk.versionsToKeep}"
	 */
	int versionsToKeep;

	/**
	 * How many versions to keep?
	 * 
	 * @parameter default-value="${beanstalk.daysToKeep}"
	 */
	int daysToKeep;

	/**
	 * Simulate deletion changing algorithm?
	 * 
	 * @parameter expr="${beanstalk.dryRun}"
	 */
	boolean dryRun = true;

	@Override
	protected Object executeInternal() throws MojoExecutionException,
	    MojoFailureException {
		boolean bVersionsToKeepDefined = (versionsToKeep > 0);
		boolean bDaysToKeepDefined = (daysToKeep > 0);

		if (!(bVersionsToKeepDefined ^ bDaysToKeepDefined))
			throw new MojoFailureException(
			    "Declare either versionsToKeep or daysToKeep, but not both nor none!");

		DescribeApplicationVersionsRequest describeApplicationVersionsRequest = new DescribeApplicationVersionsRequest()
		    .withApplicationName(applicationName);

		DescribeApplicationVersionsResult appVersions = service
		    .describeApplicationVersions(describeApplicationVersionsRequest);

		List<ApplicationVersionDescription> appVersionList = new ArrayList<ApplicationVersionDescription>(
		    appVersions.getApplicationVersions());

		int size = appVersionList.size();

		Collections.sort(appVersionList,
		    new Comparator<ApplicationVersionDescription>() {
			    @Override
			    public int compare(ApplicationVersionDescription o1,
			        ApplicationVersionDescription o2) {
				    return new CompareToBuilder().append(o1.getDateUpdated(),
				        o2.getDateUpdated()).toComparison();
			    }
		    });

		if (bDaysToKeepDefined) {
			Date now = new Date();

			for (ApplicationVersionDescription d : appVersionList) {
				long delta = now.getTime() - d.getDateUpdated().getTime();

				delta /= 1000;
				delta /= 86400;

				if (delta > daysToKeep)
					deleteVersion(d);
			}
		} else {
			while (appVersionList.size() > versionsToKeep)
				deleteVersion(appVersionList.remove(0));
		}

		getLog().info("Deleted " + (size - appVersionList.size()) + " versions.");

		return null;
	}

	void deleteVersion(ApplicationVersionDescription versionToRemove) {
		getLog().info("Must delete version: " + versionToRemove.getVersionLabel());

		DeleteApplicationVersionRequest req = new DeleteApplicationVersionRequest()
		    .withApplicationName(versionToRemove.getApplicationName())//
		    .withDeleteSourceBundle(deleteSourceBundle)//
		    .withVersionLabel(versionToRemove.getVersionLabel());

		if (!dryRun)
			service.deleteApplicationVersion(req);
	}
}
