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

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.sh.AbstractClient;

/**
 * Base implementation of Client
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Client extends AbstractClient {

  protected boolean sentSubscribeNotifications;
  protected boolean sentProfileUpdate;
  protected boolean sentUserData;
  protected boolean sentPushNotification;
  protected boolean receiveSubscribeNotifications;
  protected boolean receiveProfileUpdate;
  protected boolean receiveUserData;
  protected boolean receivePushNotification;

  protected PushNotificationRequest request;

  /**
   *
   */
  public Client() {
  }

  public void sendSubscribeNotifications() throws Exception {
    SubscribeNotificationsRequest request = new SubscribeNotificationsRequestImpl(super.clientShSession.getSessions().get(0)
        .createRequest(SubscribeNotificationsRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < Subscribe-Notifications-Request > ::= < Diameter Header: 308, REQ, PXY, 16777217 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
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
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // *[ Service-Indication ]
    // [ Send-Data-Indication ]
    // [ Server-Name ]
    // { Subs-Req-Type }
    avpSet.addAvp(Avp.SUBS_REQ_TYPE, 0, getApplicationId().getVendorId(), true, false, true);
    // *{ Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ Identity-Set ]
    // [ Expiry-Time ]
    // *[ DSAI-Tag ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientShSession.sendSubscribeNotificationsRequest(request);
    this.sentSubscribeNotifications = true;
  }

  public void sendProfileUpdate() throws Exception {
    ProfileUpdateRequest request = new ProfileUpdateRequestImpl(super.clientShSession.getSessions().get(0)
        .createRequest(ProfileUpdateRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < Profile-Update-Request > ::= < Diameter Header: 307, REQ, PXY, 16777217 >
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
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]

    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // { Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // { User-Data }
    avpSet.addAvp(Avp.USER_DATA_SH, "<xml><morexml></morexml></xml>", getApplicationId().getVendorId(), true, false, false);

    // *[ AVP ]
    super.clientShSession.sendProfileUpdateRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentProfileUpdate = true;
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
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]

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

  public void sendPushNotification() throws Exception {
    if (!this.receivePushNotification || this.request == null) {
      fail("Did not receive NOTIFICATION or answer already sent.", null);
      throw new Exception("Did not receive NOTIFICATION or answer already sent. Request: " + this.request);
    }
    PushNotificationAnswer answer = new PushNotificationAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    // < Push-Notification-Answer > ::=< Diameter Header: 309, PXY, 16777217 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }
    // [ Result-Code ]
    // [ Experimental-Result ]
    // { Auth-Session-State }
    set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    // { Origin-Realm }
    // *[ Supported-Features ]
    // *[ AVP ]
    // *[ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    super.clientShSession.sendPushNotificationAnswer(answer);
    this.sentPushNotification = true;
  }

  // ------------ event handlers;

  @Override
  public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request, SubscribeNotificationsAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receiveSubscribeNotifications = true;
  }

  @Override
  public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    receiveProfileUpdate = true;
  }

  @Override
  public void doPushNotificationRequestEvent(ClientShSession session, PushNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receivePushNotification = true;
    this.request = request;
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

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != PushNotificationRequest.code) {
      fail("Received Request with code not used by Sh!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientShSession.getSessionId().equals(request.getSessionId())) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      super.clientShSession.release();
      try {
        super.clientShSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientShSession.class, (Object) null);
        ((NetworkReqListener) this.clientShSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
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
