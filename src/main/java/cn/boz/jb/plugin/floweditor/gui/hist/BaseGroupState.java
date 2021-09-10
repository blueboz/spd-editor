package cn.boz.jb.plugin.floweditor.gui.hist;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合状态
 */
public class BaseGroupState extends BaseState {
    public List<BaseState> operateds=new ArrayList<>();

    public BaseGroupState(){ }
    public BaseGroupState(List<BaseState>operateds){
       this.operateds=operateds;
    }
}
