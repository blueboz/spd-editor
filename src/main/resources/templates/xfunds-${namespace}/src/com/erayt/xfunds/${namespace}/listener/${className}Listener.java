/*********************************************
 * Copyright (c) 2023 LI-RTP.
 * All rights reserved.
 * Created on 2023年4月27日
 *
 * Contributors:
 *     rtp - initial implementation
 *********************************************/

package com.erayt.xfunds.${namespace}.listener;

import com.erayt.solar2.engine.process.Event;
import com.erayt.solar2.engine.process.EventListener;
import com.erayt.solar2.engine.process.Task;
import com.erayt.solar2.engine.process.TaskListener;
import com.erayt.xfunds.${namespace}.domain.${className};

import java.util.Map;

public class ${className}Listener implements EventListener, TaskListener{
	@Override
	public void preprocess(Map<String, Object> context, Task task) {
		if(context==null || task==null) {
			return  ;
		}
		Object obj = context.get("pawnTrade");
		if(!(obj instanceof ${className})) {
			return ;
		}
		${className} trade = (${className})obj ;
//		trade.setTaskName(task.getTitle());
	}

	@Override
	public void process(Map<String, Object> context, Task task) {
		if(context==null|| task==null) {
			return  ;
		}
		Object obj = context.get("${beanName}");
		if(!(obj instanceof ${className})) {
			return ;
		}
		${className} trade = (${className})obj ;
//		trade.setTaskName(task.getTitle());
	}

	@Override
	public void process(Map<String, Object> context, Task task, Event event) {
		if(context==null|| task==null ||event==null) {
			return  ;
		}
		Object obj = context.get("${beanName}");
		if(!(obj instanceof ${className})) {
			return ;
		}
		${className} trade = (${className})obj ;
//		trade.setEventId(event.getId());
//		trade.setOperType(event.getDoneType());
//		trade.setTaskName(task.getTitle());
	}

}
