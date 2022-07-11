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
package de.bxservice.utils;

import org.compiere.model.I_M_ProductPrice;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class UOMProductPricingHelper {

	public static MProductPrice getMProductPrice(int M_PriceList_Version_ID, int M_Product_ID, int C_UOM_ID, String trxName) {
		final String whereClause = MProductPrice.COLUMNNAME_M_PriceList_Version_ID +"=? AND "+MProductPrice.COLUMNNAME_M_Product_ID
				+"=? AND C_UOM_ID=?";
		
		return new Query(Env.getCtx(),I_M_ProductPrice.Table_Name,  whereClause, trxName)
				.setParameters(M_PriceList_Version_ID, M_Product_ID, C_UOM_ID)
				.first();
	}
	
	public static boolean supportsPricePerUOM(int productID, int priceListVersionID) {
		MProduct product = MProduct.get(productID);
		return product != null && managesPricePerUOM(product) && 
				hasPricesPerUOM(productID, priceListVersionID) && !product.isBOM();
	}

	private static boolean managesPricePerUOM(MProduct product) {
		return product.get_ValueAsBoolean("BXS_IsPricePerUOM");
	}

	private static boolean hasPricesPerUOM(int productID, int priceListVersionID) {
		final String sql = "SELECT 1 "
				+ "FROM M_ProductPrice pp "
				+ "WHERE pp.IsActive='Y'"
				+ " AND pp.M_Product_ID=?"
				+ " AND pp.M_PriceList_Version_ID=?"
				+ " AND pp.C_UOM_ID IS NOT NULL";
		return DB.getSQLValue(null, sql, productID, priceListVersionID) > 0;
	}
	
}
