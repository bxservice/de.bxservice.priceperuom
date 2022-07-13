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

import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;

import de.bxservice.utils.UOMProductPricingHelper;

public class ProductHandler extends AbstractPOHandler {

	private MProduct product;
	
	public ProductHandler(MProduct po) {
		super(po);
		this.product = po;
	}

	@Override
	public void validateUpdate() {
		if (product.is_ValueChanged(UOMProductPricingHelper.COLUMNNAME_ManagePricePerUOM))
			checkValidChange();
	}
	
	private void checkValidChange() {
		if (isProductManagedPerUOM())
			setDefaultUOMToExistingProductPrices();
		else
			validateDisableManagePricePerUOM();
	}
	
	private boolean isProductManagedPerUOM() {
		return  product.get_ValueAsBoolean(UOMProductPricingHelper.COLUMNNAME_ManagePricePerUOM);
	}
	
	private void setDefaultUOMToExistingProductPrices() {
		List<MProductPrice> pricesWithNoUOM = UOMProductPricingHelper.getProductPricesWithNullUOM(product.getM_Product_ID());
		if (pricesWithNoUOM != null && !pricesWithNoUOM.isEmpty()) {
			for (MProductPrice productPrice : pricesWithNoUOM) {
				setDefaultUOMToProductPrice(productPrice);
			}
		}
	}
	
	private void setDefaultUOMToProductPrice(MProductPrice productPrice) {
		log.warning("Adding default UOM to product price: " + productPrice.get_ID());
		productPrice.set_ValueOfColumn("C_UOM_ID", product.getC_UOM_ID());
		productPrice.saveEx();
	}
	
	private void validateDisableManagePricePerUOM() {
		if (UOMProductPricingHelper.hasPricesForNonDefaultUOM(product)) {
			throw new AdempiereException("The product has different prices per UOM configured. Please delete the non default records before disabling the feature.");
		}
	}
}
