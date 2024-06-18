 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.server.impl.agent;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.server.api.agent.IProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author babass
 */
public class ProxyAgentImpl extends AgentImpl implements IProxy {

  private static Logger logger = LoggerFactory.getLogger(ProxyAgentImpl.class);

  /**
   * @param container
   * @param realmTable
   */
  public ProxyAgentImpl(IContainer container, IRealmTable realmTable) {
    super(container, realmTable);
    logger.info("proxy agent: created");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.server.api.agent.IAgent#processRequest(org.jdiameter.client
   * .api.IRequest, org.jdiameter.client.api.controller.IRealm)
   */
  @Override
  public Answer processRequest(IRequest request, IRealm matchedRealm) {
    logger.info("proxy agent: processRequest executed");
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.
   * api.Message, org.jdiameter.api.Message)
   */
  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    logger.info("proxy agent: receivedSuccessMessage");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  @Override
  public void timeoutExpired(Request request) {
    logger.info("proxy agent: timeoutExpired");
  }

}
