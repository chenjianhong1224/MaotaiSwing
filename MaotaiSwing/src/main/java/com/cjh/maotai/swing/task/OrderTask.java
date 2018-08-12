package com.cjh.maotai.swing.task;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cjh.maotai.swing.MainFrame;
import com.cjh.maotai.swing.beans.MaotaiSkuBean;
import com.cjh.maotai.swing.beans.ReturnResultBean;
import com.cjh.maotai.swing.beans.ViewMsgBean;
import com.cjh.maotai.swing.beans.spring.SpringContextUtils;
import com.cjh.maotai.swing.service.MaotaiService;
import com.cjh.maotai.swing.service.impl.MaotaiServiceImpl;

public class OrderTask implements Runnable {

	public OrderTask(Date beginTime, Date endTime, String auth, MaotaiSkuBean skuBean, String num, String purchaseWay,
			String taskNo) {
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.skuBean = skuBean;
		this.auth = auth;
		this.num = num;
		this.purchaseWay = purchaseWay;
		this.taskNo = taskNo;
		myFinish = false;
	}

	String taskNo;

	Date beginTime;

	Date endTime;

	String auth;

	String num;

	String purchaseWay;

	MaotaiSkuBean skuBean;

	boolean myFinish;

	private static AtomicBoolean taskFinishFlag = new AtomicBoolean(false);

	public static AtomicBoolean getTaskFinishFlag() {
		return taskFinishFlag;
	}

	public static void setTaskFinish() {
		taskFinishFlag.set(true);
	}

	@Override
	public void run() {
		ViewMsgBean msg = new ViewMsgBean();
		msg.setTaskNo(taskNo);
		try {
			while (!taskFinishFlag.get()) {
				Date now = new Date();
				msg.setTime(now);
				if (now.getTime() < beginTime.getTime()) {
					msg.setMsg("还未到点, 休息中...");
					MainFrame.msgQueue.put(msg);
				}
				if (now.getTime() > endTime.getTime()) {
					msg.setMsg("到点了, 收工...");
					MainFrame.msgQueue.put(msg);
					return;
				}
				if (now.getTime() < beginTime.getTime()) {
					if ((beginTime.getTime() - now.getTime()) < (1 * 1000 * 60)) { // 小于1分钟
						Thread.sleep(1);
						continue; // 提高刷新频率
					}
					Thread.sleep(1000 * 2); // 2秒刷新一次，避免session失效
					continue;
				}
				msg.setMsg("到点了, 开始干活...");
				MainFrame.msgQueue.put(msg);
				if (now.getTime() > endTime.getTime()) {
					msg.setMsg("到点了, 收工...");
					MainFrame.msgQueue.put(msg);
					return;
				}
				long startTime = System.currentTimeMillis();
				MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
						.getBean("maotaiServiceImpl");
				ReturnResultBean resultBean = maotaiService.order(auth, skuBean, num, purchaseWay);
				if (resultBean.getResultCode() == 0) {
					msg.setMsg("下单成功啦, 收队...");
					taskFinishFlag.set(true);
					myFinish = true;
					MainFrame.msgQueue.put(msg);
					return;
				} else {
					msg.setMsg(resultBean.getReturnMsg());
					MainFrame.msgQueue.put(msg);
				}
				double excTime = (double) (System.currentTimeMillis() - startTime) / 1000;
				Thread.sleep(500);
			}
		} catch (InterruptedException e1) {
			msg.setMsg("任务中断..");
			try {
				MainFrame.msgQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e2) {
			msg.setMsg("任务异常 " + e2.getMessage());
			try {
				MainFrame.msgQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			if (!myFinish) {
				msg.setMsg("任务退出");
				try {
					MainFrame.msgQueue.put(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
