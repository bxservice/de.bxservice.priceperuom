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
import java.math.RoundingMode;

import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MProductPricing;
import org.compiere.util.Env;

import de.bxservice.utils.UOMProductPricingHelper;

public class ProductPerUOMPricing extends MProductPricing {

	private int		 	m_precision = -1;
	private int C_UOM_ID = 0;
	private boolean 	calculated = false;
	private BigDecimal 	priceList = Env.ZERO;
	private BigDecimal 	priceStd = Env.ZERO;
	private BigDecimal 	priceLimit = Env.ZERO;
	private int 		C_Currency_ID = 0;
	private boolean		enforcePriceLimit = false;

	@Override
	public boolean calculatePrice() {
		if (m_M_Product_ID <= 0 || m_M_PriceList_Version_ID <= 0)
			return false;

		if (!supportsPricePerUOM())
			return super.calculatePrice();

		calculated = calculatePricePerUOM();
		setPrecision();

		return calculated;
	}

	private boolean supportsPricePerUOM() {
		return UOMProductPricingHelper.supportsPricePerUOM(m_M_Product_ID, m_M_PriceList_Version_ID);
	}

	private boolean calculatePricePerUOM() {
		MProduct product = MProduct.get(m_M_Product_ID);
		C_UOM_ID = product.getC_UOM_ID();
		MProductPrice productPrice = UOMProductPricingHelper.getMProductPrice(m_M_PriceList_Version_ID, m_M_Product_ID,C_UOM_ID, trxName);
		setPrices(productPrice);

		MPriceList priceList = MPriceList.get(m_M_PriceList_ID);
		C_Currency_ID = priceList.getC_Currency_ID();
		enforcePriceLimit = priceList.isEnforcePriceLimit();

		return true;
	}
	
	private void setPrices(MProductPrice productPrice) {
		if (productPrice != null) {
			priceStd = productPrice.getPriceStd();
			priceList = productPrice.getPriceList();
			priceLimit = productPrice.getPriceLimit();
		}
	}

	@Override
	public BigDecimal getDiscount() {
		if (supportsPricePerUOM())
			setM_PriceList_ID(m_M_PriceList_ID);

		return super.getDiscount();
		
	}

	@Override
	public int getC_UOM_ID() {
		if (!supportsPricePerUOM())
			return super.getC_UOM_ID();
		
		return C_UOM_ID;
	}


	@Override
	public BigDecimal getPriceList() {
		if (!supportsPricePerUOM())
			return super.getPriceList();
		
		if (!calculated)
			calculatePrice();
		
		return round(priceList);
	}

	@Override
	public BigDecimal getPriceStd() {
		if (!supportsPricePerUOM())
			return super.getPriceStd();

		if (!calculated)
			calculatePrice();

		return round(priceStd);
	}

	@Override
	public BigDecimal getPriceLimit() {
		if (!supportsPricePerUOM())
			return super.getPriceLimit();
		
		if (!calculated)
			calculatePrice();
		
		return round(priceLimit);
	}

	@Override
	public int getC_Currency_ID() {
		if (!supportsPricePerUOM())
			return super.getC_Currency_ID();
		
		if (!calculated)
			calculatePrice();
		
		return C_Currency_ID;
	}
	
	private void setPrecision() {
		if (m_M_PriceList_ID != 0)
			m_precision = MPriceList.getPricePrecision(Env.getCtx(), getM_PriceList_ID());
	}	//

	private BigDecimal round(BigDecimal bd) {
		if (m_precision >= 0	//	-1 = no rounding
			&& bd.scale() > m_precision)
			return bd.setScale(m_precision, RoundingMode.HALF_UP);
		return bd;
	}	//	round
	
	@Override
	public boolean isEnforcePriceLimit() {
		if (!supportsPricePerUOM())
			return super.isEnforcePriceLimit();
		
		if (!calculated)
			calculatePrice();
		
		return enforcePriceLimit;
	}

	@Override
	public boolean isCalculated() {
		if (!supportsPricePerUOM())
			return super.isCalculated();
		
		if (!calculated)
			calculatePrice();
		
		return calculated;
	}

}
