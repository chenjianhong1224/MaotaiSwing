package com.cjh.maotai.swing.utils;

import javax.swing.JOptionPane;

import org.springframework.util.StringUtils;

import com.cjh.maotai.swing.beans.MaotaiSkuBean;
import com.cjh.maotai.swing.beans.ReturnResultBean;

public class MaotaiUrlParseUtil {

	public static ReturnResultBean parseSkuUrl(String url) {
		ReturnResultBean resultBean = new ReturnResultBean();
		MaotaiSkuBean skuBean = new MaotaiSkuBean();
		resultBean.setResultCode(-1);
		resultBean.setReturnMsg("商品url不合法");
		if (StringUtils.isEmpty(url)) {
			resultBean.setReturnMsg("商品url不能为空");
		} else {
			String sku[] = url.split("=");
			if (sku.length == 2) {
				if (sku[0].endsWith("skuId")) {
					skuBean.setSkuId(sku[1]);
					String params[] = sku[0].split("/");
					if (params.length > 0) {
						String[] itemShop = params[params.length - 1].split("-");
						if (itemShop.length == 2) {
							skuBean.setItemId(itemShop[0]);
							skuBean.setShopId(itemShop[1].substring(0, itemShop[1].indexOf(".")));
							resultBean.setResultCode(0);
							resultBean.setReturnObj(skuBean);
						}
					}
				}
			}
		}
		return resultBean;
	}

}
