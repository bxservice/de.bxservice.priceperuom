/**********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - BX Service GmbH                                      *
 **********************************************************************/
package de.bxservice.eventhandler;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;

@Component(
		reference = @Reference( 
				name = "IEventManager", bind = "bindEventManager", unbind="unbindEventManager", 
				policy = ReferencePolicy.STATIC, cardinality =ReferenceCardinality.MANDATORY, service = IEventManager.class)
		)
public class PricePerUOMEventHandler extends AbstractEventHandler {

	/** Logger */
	private static CLogger log = CLogger.getCLogger(PricePerUOMEventHandler.class);
	
	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();
		PO po = getPO(event);
		log.info(po + " Type: " + type);

		POHandler poHandler = HandlerPOFactory.getPOHandler(po);
		
		if (IEventTopics.PO_BEFORE_CHANGE.equals(type))
			poHandler.validateUpdate();
		else if (IEventTopics.PO_BEFORE_NEW.equals(type))
			poHandler.validateNew();

	}

	@Override
	protected void initialize() {
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MProduct.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MProductPrice.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_NEW, MProductPrice.Table_Name);
	}

}
