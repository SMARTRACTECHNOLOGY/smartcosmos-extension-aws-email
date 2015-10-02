package net.smartcosmos.plugin.service.aws.email;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMART COSMOS AWS SES Email Service Plugin
 * ===============================================================================
 * Copyright (C) 2013 - 2015 Smartrac Technology Fletcher, Inc.
 * ===============================================================================
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
 * #*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
 */

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import net.smartcosmos.platform.api.annotation.ServiceExtension;
import net.smartcosmos.platform.api.annotation.ServiceType;
import net.smartcosmos.platform.api.service.IEmailService;
import net.smartcosmos.platform.base.AbstractAwsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ServiceExtension(serviceType = ServiceType.EMAIL)
public class AwsEmailService extends AbstractAwsService<AWSCredentials>
        implements IEmailService
{
    private static final Logger LOG = LoggerFactory.getLogger(AwsEmailService.class);

    public AwsEmailService()
    {
        super("8AC7970C42538B3B0142538C36140005", "AWS SES Email Service");
    }

    @Override
    public void sendEmail(String to, String subject, String plainMessage, String htmlMessage)
    {
        String from = context.getConfiguration().getAdminEmailAddress();
        SendEmailRequest request = new SendEmailRequest().withSource(from);

        List<String> toAddresses = new ArrayList<String>();
        toAddresses.add(to);

        Destination destination = new Destination().withToAddresses(toAddresses);
        request.setDestination(destination);

        Content subjContent = new Content().withData(subject);
        Message msg = new Message().withSubject(subjContent);

        Content textContent = new Content().withData(plainMessage);
        Content htmlContent = new Content().withData(htmlMessage);

        Body body = new Body().withHtml(htmlContent).withText(textContent);
        msg.setBody(body);

        request.setMessage(msg);

        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);

        try
        {
            client.sendEmail(request);
            LOG.info("Registration confirmation sent to email " + to);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isHealthy()
    {
        try
        {
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
            client.getSendQuota();
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    @Override
    protected AWSCredentials createCloudCredentials(String accessKey, String secretAccessKey)
    {
        return new BasicAWSCredentials(accessKey, secretAccessKey);
    }
}
