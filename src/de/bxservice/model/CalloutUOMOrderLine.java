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
package de.bxservice.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MProductPrice;
import org.compiere.util.Env;

import de.bxservice.utils.UOMProductPricingHelper;

public class CalloutUOMOrderLine implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		int M_PriceList_Version_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_Version_ID");
		int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");

		if (UOMProductPricingHelper.supportsPricePerUOM(M_Product_ID, M_PriceList_Version_ID) && value != null) {
			int C_UOM_ID = ((Integer) value).intValue();
			MProductPrice productPrice = getMProductPrice(M_Product_ID,M_PriceList_Version_ID,C_UOM_ID);
			if (productPrice != null) {
				BigDecimal priceEntered = productPrice.getPriceStd();
				mTab.setValue("PriceEntered", priceEntered);
			}
		}
		return null;
	}
	
	private MProductPrice getMProductPrice(int M_Product_ID, int M_PriceList_Version_ID, int C_UOM_ID) {
		return UOMProductPricingHelper.getMProductPrice(M_PriceList_Version_ID, M_Product_ID, C_UOM_ID, null);
	}

}
