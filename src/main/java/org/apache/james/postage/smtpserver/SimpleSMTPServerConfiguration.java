/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.postage.smtpserver;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.james.smtpserver.CoreCmdHandlerLoader;
import org.apache.james.smtpserver.fastfail.DNSRBLHandler;
import org.apache.james.smtpserver.fastfail.MaxRcptHandler;
import org.apache.james.smtpserver.fastfail.ResolvableEhloHeloHandler;
import org.apache.james.smtpserver.fastfail.ReverseEqualsEhloHeloHandler;
import org.apache.james.smtpserver.fastfail.ValidSenderDomainHandler;

public class SimpleSMTPServerConfiguration extends DefaultConfigurationBuilder {
    private static final long serialVersionUID = 6459491961488047925L;
    private int smtpListenerPort;
    private int maxMessageSizeKB = 0;
    private String authorizedAddresses = "127.0.0.0/8";
    private String authorizingMode = "false";
    private boolean verifyIdentity = false;
    private Integer connectionLimit = null;
    private Integer connectionBacklog = null;
    private boolean heloResolv = false;
    private boolean ehloResolv = false;
    private boolean senderDomainResolv = false;
    private boolean checkAuthNetworks = false;
    private boolean heloEhloEnforcement = true;
    private boolean reverseEqualsHelo = false;
    private boolean reverseEqualsEhlo = false;
    private int maxRcpt = 0;
    private boolean useRBL = false;
    private boolean addressBracketsEnforcement = true;
    private boolean startTLS = false;
    
    public SimpleSMTPServerConfiguration(int smtpListenerPort) {
        this.smtpListenerPort = smtpListenerPort;
    }

    public void init() throws ConfigurationException {

        addProperty("[@enabled]", true);

        addProperty("bind", "127.0.0.1:" + smtpListenerPort);
        if (connectionLimit != null)
            addProperty("connectionLimit", "" + connectionLimit.intValue());
        if (connectionBacklog != null)
            addProperty("connectionBacklog", "" + connectionBacklog.intValue());

        addProperty("helloName", "myMailServer");
        addProperty("connectiontimeout", 360000);
        addProperty("authorizedAddresses", authorizedAddresses);
        addProperty("maxmessagesize", maxMessageSizeKB);
        addProperty("authRequired", authorizingMode);
        addProperty("heloEhloEnforcement", heloEhloEnforcement);
        addProperty("addressBracketsEnforcement", addressBracketsEnforcement);

        addProperty("tls.[@startTLS]", startTLS);
        addProperty("tls.keystore", "file://conf/test_keystore");
        addProperty("tls.secret", "jamestest");
        if (verifyIdentity)
            addProperty("verifyIdentity", verifyIdentity);

        // add the rbl handler
        if (useRBL) {

            addProperty("handlerchain.handler.[@class]", DNSRBLHandler.class.getName());
            addProperty("handlerchain.handler.rblservers.blacklist", "bl.spamcop.net.");
        }
        if (heloResolv || ehloResolv) {
            addProperty("handlerchain.handler.[@class]", ResolvableEhloHeloHandler.class.getName());
            addProperty("handlerchain.handler.checkAuthNetworks", checkAuthNetworks);
        }
        if (reverseEqualsHelo || reverseEqualsEhlo) {
            addProperty("handlerchain.handler.[@class]", ReverseEqualsEhloHeloHandler.class.getName());
            addProperty("handlerchain.handler.checkAuthNetworks", checkAuthNetworks);
        }
        if (senderDomainResolv) {
            addProperty("handlerchain.handler.[@class]", ValidSenderDomainHandler.class.getName());
            addProperty("handlerchain.handler.checkAuthNetworks", checkAuthNetworks);
        }
        if (maxRcpt > 0) {
            addProperty("handlerchain.handler.[@class]", MaxRcptHandler.class.getName());
            addProperty("handlerchain.handler.maxRcpt", maxRcpt);
        }
        addProperty("handlerchain.[@coreHandlersPackage]", CoreCmdHandlerLoader.class.getName());
    }

}
