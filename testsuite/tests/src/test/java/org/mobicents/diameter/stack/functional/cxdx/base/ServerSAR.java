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
package org.mobicents.diameter.stack.functional.cxdx.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.cxdx.AbstractServer;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerSAR extends AbstractServer {

  protected boolean receivedServerAssignment;
  protected boolean sentServerAssignment;

  protected JServerAssignmentRequest request;

  /**
   *
   */
  public ServerSAR() {
  }

  public void sendServerAssignment() throws Exception {
    if (!receivedServerAssignment || request == null) {
      fail("Did not receive SAR or answer already sent.", null);
      throw new Exception("Did not receive SAR or answer already sent. Request: " + this.request);
    }

    JServerAssignmentAnswer answer = new JServerAssignmentAnswerImpl((Request) this.request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    request = null;

    // <Server-Assignment-Answer> ::= < Diameter Header: 301, PXY, 16777216 >
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
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }
    // { Origin-Host }
    // { Origin-Realm }
    // [ User-Name ]
    // *[ Supported-Features ]
    // [ User-Data ]
    // [ Charging-Information ]
    // [ Associated-Identities ]
    // [ Loose-Route-Indication ]
    // *[ SCSCF-Restoration-Info ]
    // [ Associated-Registered-Identities ]
    // [ Server-Name ]
    // *[ AVP ]
    // *[ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    this.serverCxDxSession.sendServerAssignmentAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentServerAssignment = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JServerAssignmentRequest.code) {
      fail("Received Request with code not used by CxDx!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverCxDxSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.serverCxDxSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerCxDxSession.class, (Object) null);
        ((NetworkReqListener) this.serverCxDxSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  @Override
  public void doServerAssignmentRequest(ServerCxDxSession session, JServerAssignmentRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedServerAssignment) {
      fail("Received SAR more than once", null);
      return;
    }

    this.receivedServerAssignment = true;
    this.request = request;
  }

  public boolean isReceivedServerAssignment() {
    return receivedServerAssignment;
  }

  public boolean isSentServerAssignment() {
    return sentServerAssignment;
  }

}
