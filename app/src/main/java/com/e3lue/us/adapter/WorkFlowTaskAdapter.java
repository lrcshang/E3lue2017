package com.e3lue.us.adapter;

import android.content.Context;

import com.e3lue.us.R;
import com.e3lue.us.model.WorkFlowTask;
import com.e3lue.us.ui.quickadapter.BaseAdapterHelper;
import com.e3lue.us.ui.quickadapter.QuickAdapter;

/**
 * Created by Leo on 2017/4/25.
 */

public class WorkFlowTaskAdapter {

    public static QuickAdapter<WorkFlowTask> getAdapter(Context context) {
        QuickAdapter<WorkFlowTask> adapter = new QuickAdapter<WorkFlowTask>(context, R.layout.tasks_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, WorkFlowTask entity) {
                String user = "<font color='blue'>" + entity.getReceiveName() + "</font><font color='gray'>&#160;-&#160;" + entity.getStepName() + "</font>";
                if (entity.getStatus() < 2)
                    user += "&#160;(<font color='red'>未处理</font>)";
                String completetime = "";
                String Comment = entity.getComment() == null ? "" : entity.getComment();
                String Note = "";
                if (entity.getNote() != null && entity.getNote().trim().length() > 0) Note = "退回任务";
                if (!entity.getCompletedTime1().contains("1900")) {
                    completetime = entity.getCompletedTime1();
                    if (entity.getStatus() == 3) completetime += "&#160;退回";
                    completetime += "&#160;处理";
                }
                int pos = this.getCount() - helper.getPosition();
                helper.setText(R.id.task_list_item_num, "" + pos)
                        .setText(R.id.task_list_item_user, user)
                        .setText(R.id.task_list_item_content, Comment)
                        .setText(R.id.task_list_item_complated_date, completetime)
                        .setText(R.id.task_list_item_receive_date, entity.getReceiveTime() + "&#160;接收 " + Note);
                if (Comment == "") helper.setVisible(R.id.task_list_item_content, false);
                if (completetime == "")
                    helper.setVisible(R.id.task_list_item_complated_date, false);
            }
        };
        return adapter;
    }
}
