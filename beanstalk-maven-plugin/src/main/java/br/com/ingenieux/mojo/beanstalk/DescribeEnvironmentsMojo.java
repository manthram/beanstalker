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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;

/**
 * Describe running environments
 * 
 * See the docs for the <a href=
 * "http://docs.amazonwebservices.com/elasticbeanstalk/latest/api/API_DescribeEnvironments.html"
 * >DescribeEnvironments API</a> call.
 * 
 * @author Aldrin Leal
 * @goal describe-environments
 */
public class DescribeEnvironmentsMojo extends AbstractBeanstalkMojo {
	/**
	 * Include Deleted?
	 * 
	 * @parameter expr="${includeDeleted}"
	 */
	boolean includeDeleted = true;

	@Override
	protected Object executeInternal() throws MojoExecutionException,
	    MojoFailureException {
		DescribeEnvironmentsRequest req = new DescribeEnvironmentsRequest();

		req.setApplicationName(applicationName);
		req.setIncludeDeleted(includeDeleted);
		req.setVersionLabel(versionLabel);

		return service.describeEnvironments(req);
	}

}
