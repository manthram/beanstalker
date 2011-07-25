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

import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;

/**
 * Creates and Launches an Elastic Beanstalk Environment
 * 
 * See the docs for the <a href=
 * "http://docs.amazonwebservices.com/elasticbeanstalk/latest/api/API_CreateEnvironment.html"
 * >CreateEnvironment API</a> call.
 * 
 * @goal create-environment
 */
public class CreateEnvironmentMojo extends AbstractBeanstalkMojo {
	@Override
	protected Object executeInternal() throws MojoExecutionException,
	    MojoFailureException {
		CreateEnvironmentRequest request = new CreateEnvironmentRequest();

		request.setApplicationName(applicationName);
		request.setCNAMEPrefix(cnamePrefix);
		request.setDescription(applicationDescription);
		request.setEnvironmentName(environmentName);

		request.setOptionSettings(getOptionSettings());

		request.setOptionsToRemove(getOptionsToRemove());

		request.setSolutionStackName(solutionStack);

		request.setTemplateName(templateName);

		request.setVersionLabel(versionLabel);

		return service.createEnvironment(request);
	}
}
