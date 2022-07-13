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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;

import de.bxservice.utils.UOMProductPricingHelper;

public class ProductPriceHandler extends AbstractPOHandler {

	private MProductPrice productPrice;
	
	public ProductPriceHandler(MProductPrice po) {
		super(po);
		this.productPrice = po;
	}

	@Override
	public void validateNew() {
		MProduct product = MProduct.get(productPrice.getM_Product_ID());
		if (UOMProductPricingHelper.managesPricePerUOM(product) && productPrice.get_ValueAsInt("C_UOM_ID") <= 0) {
			throw new AdempiereException("UOM Price is mandatory for this product, please fill it up before saving.");
		}
	}

}
