package com.cjh.maotai.swing.task;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;

import com.cjh.maotai.swing.MainFrame;
import com.cjh.maotai.swing.beans.ViewMsgBean;

public class ViewTask implements Runnable {

	public ViewTask(JTable table) {
		this.table = table;
	}

	JTable table;

	@Override
	public void run() {
		while (!OrderTask.getTaskFinishFlag().get()) {
			try {
				ViewMsgBean msgBean = MainFrame.msgQueue.poll(300, TimeUnit.MILLISECONDS);
				if (msgBean != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					table.setValueAt(sdf.format(msgBean.getTime()), Integer.valueOf(msgBean.getTaskNo()) - 1, 1);
					table.setValueAt(msgBean.getMsg(), Integer.valueOf(msgBean.getTaskNo()) - 1, 2);
					table.validate();
				}
			} catch (InterruptedException e) {
				OrderTask.getTaskFinishFlag().set(true);
				return;
			}
		}
	}

}
