/**
 * Copyright (C) 2017 Clearstream.TV, Inc. All Rights Reserved.
 * Proprietary and confidential.
 *
 */
package com.bundle.notification;

import java.io.IOException;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.json.JSONException;

import com.sendgrid.*;

import com.bundle.misc.SystemVariable;
import com.bundle.models.TaskDetails;
import com.bundle.utility.Constants;
import com.bundle.utility.ContextScope;
import com.bundle.utility.PropertyFileReader;

/**
 * @author Clearstream
 */
public class EmailUtility
{
	private static final Logger LOG = LogManager.getLogger(EmailUtility.class);

	/**
	 * Publishing the Test results via email v2.0 - moustache
	 * 
	 * @param vastURLToTest
	 * @param username
	 * @param userEmail
	 * @param additionalParameters
	 * @param eventMaps
	 * @param browserName
	 * @throws IOException
	 * @throws JSONException
	 */
	public void publishTestResult(String vastURLToTest, String username, String userEmail, String additionalParameters,
			List<String> eventMaps, String browserName, ContextScope scope, String taskGroupId)
			throws IOException, JSONException
	{
		try
		{
			Email from = new Email(SystemVariable.EMAIL_FROM.value(scope));

			String subject = SystemVariable.EMAIL_SUBJECT.value(scope) + " | Ref #: " + taskGroupId;

			Email to = new Email(userEmail != null && userEmail.length() > 2 ? userEmail
					: PropertyFileReader.getProperty(Constants.EMAIL_TO_LIST));
			EmailTemplate emailTemplate = new EmailTemplate();

			String emailContent = emailTemplate.getEmailTemplate(vastURLToTest, username, additionalParameters,
					eventMaps, browserName);

			Content content = new Content("text/html", emailContent);
			Mail mail = new Mail(from, subject, to, content);
			SendGrid sg = new SendGrid(SystemVariable.SEND_GRID_AUTH_TOKEN.value(scope));
			Request request = new Request();

			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			Response response = sg.api(request);
			LOG.info(response.statusCode);
			LOG.info(response.body);
			LOG.info(response.headers);
		}
		catch (IOException ex)
		{
			LOG.error("Exception occurred while sending email...");
			ex.printStackTrace();
		}
	}

	public void publishTestResult(TaskDetails taskDetails, ContextScope scope, String expectedExecutionTime,
			String taskGroupId)
	{
		LOG.info("Start publishing email");
		Email from = new Email(SystemVariable.EMAIL_FROM.value(scope));

		String subject = SystemVariable.EMAIL_SUBJECT_SCHEDULE.value(scope) + " | Ref #: " + taskGroupId;

		String userEmail = taskDetails.getUserEmail();

		Email to = new Email(userEmail != null && userEmail.length() > 2 ? userEmail
				: PropertyFileReader.getProperty(Constants.EMAIL_TO_LIST));
		EmailTemplate emailTemplate = new EmailTemplate();
		try
		{
			String emailContent = emailTemplate.getEmailTemplateExecutionReminder(taskDetails, expectedExecutionTime);

			Content content = new Content("text/html", emailContent);
			Mail mail = new Mail(from, subject, to, content);
			SendGrid sg = new SendGrid(SystemVariable.SEND_GRID_AUTH_TOKEN.value(scope));
			Request request = new Request();

			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			Response response = sg.api(request);
			LOG.info(response.statusCode);
			LOG.info(response.body);
			LOG.info(response.headers);
		}
		catch (IOException ex)
		{
			LOG.error("Exception occurred while sending email...");
			ex.printStackTrace();
		}
	}

	public void publishTestResult(String userName, String userEmail, List<String> allTasks, String browserName,
			String platformName, ContextScope scope, String masterTaskGroupId)
	{
		LOG.info("Start publishing email");
		Email from = new Email(SystemVariable.EMAIL_FROM.value(scope));

		String subject = SystemVariable.EMAIL_SUBJECT.value(scope) + " | Ref Id: " + masterTaskGroupId;

		Email to = new Email(userEmail != null && userEmail.length() > 2 ? userEmail
				: PropertyFileReader.getProperty(Constants.EMAIL_TO_LIST));

		EmailTemplate emailTemplate = new EmailTemplate();

		Personalization personalization = new Personalization();
		
	//	personalization.addTo(to);
		if (SystemVariable.RECIPIENT_CC_EMAILS.value(scope) != null)
		{
			Email cc = new Email(SystemVariable.RECIPIENT_CC_EMAILS.value(scope));
			personalization.addCc(cc);
		}
		try
		{
			String emailContent = emailTemplate.getEmailTemplateBulk(scope, allTasks);

			Content content = new Content("text/html", emailContent);
			Mail mail = new Mail(from, subject, to, content);
			mail.addPersonalization(personalization);
			SendGrid sg = new SendGrid(SystemVariable.SEND_GRID_AUTH_TOKEN.value(scope));
			Request request = new Request();

			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			Response response = sg.api(request);
			LOG.info(response.statusCode);
			LOG.info(response.body);
			LOG.info(response.headers);
		}
		catch (IOException ex)
		{
			LOG.error("Exception occurred while sending email...");
			ex.printStackTrace();
		}
	}
}
