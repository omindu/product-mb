/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mb.integration.tests.amqp.load;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mb.integration.common.clients.AndesClient;
import org.wso2.mb.integration.common.clients.AndesClientTemp;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSConsumerClientConfiguration;
import org.wso2.mb.integration.common.clients.configurations.AndesJMSPublisherClientConfiguration;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientConstants;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientException;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtils;
import org.wso2.mb.integration.common.clients.operations.utils.AndesClientUtilsTemp;
import org.wso2.mb.integration.common.clients.operations.utils.ExchangeType;
import org.wso2.mb.integration.common.utils.backend.MBIntegrationBaseTest;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Send large messages (1MB and 10MB) to message broker and check if they are received
 */
public class TopicLargeMessagePublishConsumeTestCase extends MBIntegrationBaseTest {

    /**
     * Initialize the test as super tenant user.
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    @BeforeClass(alwaysRun = true)
    public void init() throws XPathExpressionException {
        super.init(TestUserMode.SUPER_TENANT_USER);
        AndesClientUtils.sleepForInterval(15000);
    }

    /**
     * Send 10 messages of 1MB value and check 10 messages are received by the consumer.
     *
     * @throws AndesClientException
     * @throws IOException
     * @throws NamingException
     * @throws JMSException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicOneMBSizeMessageSendReceiveTestCase()
            throws AndesClientException, IOException, NamingException, JMSException {
        long sendCount = 10;

        String pathOfSampleFileToReadContent = System.getProperty("resources.dir") + File.separator + "sample.xml";
        String pathOfFileToReadContent = System.getProperty("resources.dir") + File.separator + "pom1mb.xml";
        AndesClientUtils.createTestFileToSend(pathOfSampleFileToReadContent, pathOfFileToReadContent, 1024);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "singleLargeTopic1MB");
        consumerConfig.setMaximumMessagesToReceived(sendCount);
        consumerConfig.setPrintsPerMessageCount(sendCount / 10L);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "singleLargeTopic1MB");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setPrintsPerMessageCount(sendCount / 10L);
        publisherConfig.setReadMessagesFromFilePath(pathOfFileToReadContent);   // Setting file to be sent by publisher

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig);
        publisherClient.startClient();

        AndesClientUtils.waitUntilNoMessagesAreReceivedAndShutdownClients(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), sendCount, "Message receiving failed.");
    }

    /**
     * Send 10 messages of 10MB value and check 10 messages are received by the consumer.
     *
     * @throws AndesClientException
     * @throws NamingException
     * @throws JMSException
     * @throws IOException
     */
    @Test(groups = {"wso2.mb", "topic"})
    public void performTopicTenMBSizeMessageSendReceiveTestCase()
            throws AndesClientException, NamingException, JMSException, IOException {
        long sendCount = 10L;

        String pathOfSampleFileToReadContent = System.getProperty("resources.dir") + File.separator + "sample.xml";
        String pathOfFileToReadContent = System.getProperty("resources.dir") + File.separator + "pom1mb.xml";
        AndesClientUtils.createTestFileToSend(pathOfSampleFileToReadContent, pathOfFileToReadContent, 1024);

        // Creating a consumer client configuration
        AndesJMSConsumerClientConfiguration consumerConfig = new AndesJMSConsumerClientConfiguration(ExchangeType.QUEUE, "singleLargeTopic10MB");
        consumerConfig.setMaximumMessagesToReceived(sendCount);

        // Creating a publisher client configuration
        AndesJMSPublisherClientConfiguration publisherConfig = new AndesJMSPublisherClientConfiguration(ExchangeType.QUEUE, "singleLargeTopic10MB");
        publisherConfig.setNumberOfMessagesToSend(sendCount);
        publisherConfig.setReadMessagesFromFilePath(pathOfFileToReadContent);

        // Creating clients
        AndesClient consumerClient = new AndesClient(consumerConfig);
        consumerClient.startClient();

        AndesClient publisherClient = new AndesClient(publisherConfig);
        publisherClient.startClient();

        AndesClientUtils.waitUntilNoMessagesAreReceivedAndShutdownClients(consumerClient, AndesClientConstants.DEFAULT_RUN_TIME);

        // Evaluating
        Assert.assertEquals(publisherClient.getSentMessageCount(), sendCount, "Message sending failed");
        Assert.assertEquals(consumerClient.getReceivedMessageCount(), sendCount, "Message receiving failed.");
    }
}
