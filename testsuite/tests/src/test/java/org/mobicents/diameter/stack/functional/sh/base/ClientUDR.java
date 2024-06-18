/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional.sh.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.sh.AbstractClient;

/**
 * Base implementation of Client
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ClientUDR extends AbstractClient {

  protected boolean sentSubscribeNotifications;
  protected boolean sentProfileUpdate;
  protected boolean sentUserData;
  protected boolean sentPushNotification;
  protected boolean receiveSubscribeNotifications;
  protected boolean receiveProfileUpdate;
  protected boolean receiveUserData;
  protected boolean receivePushNotification;

  /**
   *
   */
  public ClientUDR() {
  }

  public void sendUserData() throws Exception {
    UserDataRequest request =
        new UserDataRequestImpl(super.clientShSession.getSessions().get(0).createRequest(UserDataRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < User-Data -Request> ::= < Diameter Header: 306, REQ, PXY, 16777217 >
    // < Session-Id >
    // { Auth-Session-State }
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // *[ Supported-Features ]
    // { User-Identity }
    AvpSet uiSet = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    uiSet.addAvp(Avp.PUBLIC_IDENTITY, "public-identity", getApplicationId().getVendorId(), true, false, false);
    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // [ Server-Name ]
    // *[ Service-Indication ]
    // *{ Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ Identity-Set ]
    // [ Requested-Domain ]
    // [ Current-Location ]
    // *[ DSAI-Tag ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientShSession.sendUserDataRequest(request);
    this.sentUserData = true;
  }

  // ------------ event handlers;

  @Override
  public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request, SubscribeNotificationsAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receiveSubscribeNotifications = true;
    fail("Received \"SNA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    receiveProfileUpdate = true;
    fail("Received \"PUA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doPushNotificationRequestEvent(ClientShSession session, PushNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receivePushNotification = true;
    fail("Received \"PNR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doUserDataAnswerEvent(ClientShSession session, UserDataRequest request, UserDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receiveUserData = true;
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  protected String getClientURI() {
    return clientURI;
  }

  public boolean isSentSubscribeNotifications() {
    return sentSubscribeNotifications;
  }

  public boolean isSentProfileUpdate() {
    return sentProfileUpdate;
  }

  public boolean isSentUserData() {
    return sentUserData;
  }

  public boolean isSentPushNotification() {
    return sentPushNotification;
  }

  public boolean isReceiveSubscribeNotifications() {
    return receiveSubscribeNotifications;
  }

  public boolean isReceiveProfileUpdate() {
    return receiveProfileUpdate;
  }

  public boolean isReceiveUserData() {
    return receiveUserData;
  }

  public boolean isReceivePushNotification() {
    return receivePushNotification;
  }

}
