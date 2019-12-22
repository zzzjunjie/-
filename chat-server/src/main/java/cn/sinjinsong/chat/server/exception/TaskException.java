package cn.sinjinsong.chat.server.exception;

import cn.sinjinsong.common.domain.Task;
import lombok.Data;

@Data
public class TaskException extends RuntimeException{
    private Task info;
    public TaskException(Task info){
        super(info.getDesc()+"任务执行失败");
        this.info = info;
    }
}
